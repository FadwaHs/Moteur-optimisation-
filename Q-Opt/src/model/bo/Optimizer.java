/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bo;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import model.service.CatalogueService;

/**
 *
 * @author H4NANE
 */
public class Optimizer {

    private List<Noeud> plans_log;
    private List<List<Noeud>> plans_phy;
    private HashMap<Noeud, Noeud> opt_plan_mat;
    private HashMap<Noeud, Noeud> opt_plan_pipe;

    public Optimizer(List<Noeud> pl, List<List<Noeud>> pp) {
        plans_log = pl;
        plans_phy = pp;
        opt_plan_mat = new HashMap<Noeud, Noeud>();
        opt_plan_pipe = new HashMap<Noeud, Noeud>();
        find_Opt_Mat();
        find_Opt_Pipe();
    }

    public HashMap<Noeud, Noeud> getOpt_plan_mat() {
        return opt_plan_mat;
    }

    public HashMap<Noeud, Noeud> getOpt_plan_pipe() {
        return opt_plan_pipe;
    }

    public void find_Opt_Mat() {
        double cout = plans_phy.get(0).get(0).exec_Materialization();
        int i = 0;
        for (List<Noeud> plans : plans_phy) {
            for (Noeud plan : plans) {
                if (cout >= plan.exec_Materialization()) {
                    cout = plan.exec_Materialization();
                    opt_plan_mat.clear();
                    opt_plan_mat.put(plans_log.get(i), plan);
                }
            }
            i++;
        }
    }

    public void find_Opt_Pipe() {
        double cout = plans_phy.get(0).get(0).exec_Pipeline(plans_phy.get(0).get(0).cout, 0);
        int i = 0;
        for (List<Noeud> plans : plans_phy) {
            for (Noeud plan : plans) {
                if (cout >= plan.exec_Pipeline(plan.cout, 0)) {
                    cout = plan.exec_Pipeline(plan.cout, 0);
                    opt_plan_pipe.clear();
                    opt_plan_pipe.put(plans_log.get(i), plan);
                }
            }
            i++;
        }
    }

    public String rewrite_Query(Noeud noeud, String query) {
        if (noeud.content.getData().indexOf("Project") == 0) {
            String content = noeud.content.getData();
            String[] attributes = content.substring(content.indexOf("(") + 1, content.indexOf(")")).split("(,)");
            String q = "SELECT ";
            for (int i = 0; i < attributes.length - 1; i++) {
                q = q.concat(getRelationName(attributes[i], noeud.gauche) + "." + attributes[i] + ", ");
            }
            q = q.concat(getRelationName(attributes[attributes.length - 1], noeud.gauche) + "." + attributes[attributes.length - 1] + " ");
            return rewrite_Query(noeud.gauche, q) + ";";
        }
        if (noeud.content.getData().indexOf("Selection") == 0) {
            String predicate = transform_Predicate(noeud);
            if (query.indexOf("WHERE") == -1) {
                return rewrite_Query(noeud.gauche, query).concat("\nWHERE " + predicate + " ");
            } else {
                return rewrite_Query(noeud.gauche, query).concat("\nAND " + predicate + " ");
            }
        }
        if (noeud.content.getData().indexOf("Join") == 0) {
            String content = noeud.content.getData();
            if (content.indexOf("(") > -1) {
                String predicate = transform_Predicate(noeud);
                query = rewrite_Query(noeud.droit, rewrite_Query(noeud.gauche, query));
                if (query.indexOf("WHERE") == -1) {
                    return query.concat("\nWHERE " + predicate + " ");
                } else {
                    return query.concat("\nAND " + predicate + " ");
                }
            }

        } else {
            if (query.indexOf("FROM") == -1) {
                return query.concat("\nFROM " + noeud.content.getData() + " ");
            } else {
                if (query.indexOf("WHERE") == -1) {
                    return query.concat(" , " + noeud.content.getData());
                } else {
                    return query.substring(0, query.indexOf("WHERE") - 1) + ", " + noeud.content.getData() + " \n" + query.substring(query.indexOf("WHERE"));
                }
            }
        }
        return "";
    }

    String getRelationName(String attribute, Noeud noeud) {
        String name = CatalogueService.getRelationIndexed(noeud, attribute, 2);
        if (name.length() > 0) {
            return name;
        }
        name = CatalogueService.getRelationIndexed(noeud, attribute, 1);
        if (name.length() > 0) {
            return name;
        }
        return CatalogueService.getRelationIndexed(noeud, attribute, 0);
    }

    public String transform_Predicate(Noeud noeud) {

        String content = noeud.content.getData();
        String[] conds = null;
        String[] up_op = null;
        conds = content.substring(content.indexOf("(") + 1, content.indexOf(")")).split("[OR]|[AND]");
        up_op = new String[conds.length - 1];
        for (int i = 0; i < conds.length - 1; i++) {
            up_op[i] = content.substring(content.indexOf(conds[i]) + conds[i].length(), content.indexOf(conds[i + 1]));
        }
        String predicate = new String();
        for (int i = 0; i < conds.length; i++) {
            String op1, oper, op2;
            String[] operands = conds[i].split("[=]|[!=]|[>]|[>=]|[<]|[<=]");
            if (Pattern.matches("[0-9]+", operands[1]) || Pattern.matches("(')[a-zA-Z0-9]+(')", operands[1])) {
                op1 = getRelationName(operands[0], noeud) + "." + operands[0];
                oper = conds[i].substring(conds[i].indexOf(operands[0]) + operands[0].length(), conds[i].indexOf(operands[1]));
                predicate = predicate.concat(op1 + oper + operands[1]);
            } else {
                if (CatalogueService.getRelationIndexed(noeud, operands[0], 2).length() > 0) {
                    op1 = CatalogueService.getRelationIndexed(noeud, operands[0], 2) + "." + operands[0];
                    oper = conds[i].substring(operands[0].length(), conds[i].lastIndexOf(operands[1]));
                    if (CatalogueService.getRelationIndexed(noeud, operands[1], 1).length() > 0) {
                        op2 = CatalogueService.getRelationIndexed(noeud, operands[1], 1) + "." + operands[1];
                    } else {
                        op2 = CatalogueService.getRelationIndexed(noeud, operands[1], 0);
                    }
                    predicate = op1.concat(oper.concat(op2));
                } else {
                    op1 = getRelationName(operands[0], noeud) + "." + operands[0];
                    oper = conds[i].substring(conds[i].indexOf(operands[0]) + operands[0].length(), conds[i].indexOf(operands[1]));
                    op2 = getRelationName(operands[1], noeud) + "." + operands[1];
                    predicate = op1.concat(oper.concat(op2));
                }
            }
            if (i < conds.length - 2) {
                predicate = predicate.concat(up_op[i]);
            }
        }
        return predicate;
    }

}
