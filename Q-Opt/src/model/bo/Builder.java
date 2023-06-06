package model.bo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.*;
import java.util.regex.Pattern;

/**
 *
 * @author H4NANE
 */
public class Builder {

    private String corps;
    private Noeud initPlan;

    public Builder(String body) {
        this.corps = body;
        initPlan = null;
    }

    public Noeud getInitPlan() {
        return initPlan;
    }

    //table t, etudiant where etudiant.c = hgdh
    public void createInitPlan() {

        // select T1.c1, T2.c2 from table1 T1, table2 T2, where T1.id=T2.EID and T1.c1='string' ;
        // select T1.c1, T2.c2, T3.c3 from table1 T1, table2 T2, table3 T3 where T1.id=T2.EID and T2.A = T3.B and T1.c1='string' AND T2.c3 >T2.c4;
        Noeud racine = null;
        corps = corps.substring(0, corps.length() - 1).toLowerCase().replaceAll("\n", "");
        //corps = corps.substring(0, corps.indexOf(";"));
        //extract select clause
        String select_cl = corps.substring("select".length(), corps.indexOf("from"));
        select_cl = select_cl.trim();
        racine = Select_Clause(select_cl);
        //extract from clause
        String from_cl = new String();
        //extraire la clause from
        if (corps.indexOf("where") > 0) {
            from_cl = corps.substring(corps.indexOf("from") + "from".length(), corps.indexOf("where"));
        } else {
            from_cl = corps.substring(corps.indexOf("from") + "from".length());
        }
        from_cl = from_cl.trim();
        if (racine == null) {
            racine = From_Clause(from_cl);
        } else {
            racine.gauche = From_Clause(from_cl);
        }
        //in case the query has a where clause
        if (corps.indexOf("where") > 0) {
            String where_cl = corps.substring(corps.indexOf("where") + "where".length()).trim();
            racine = Where_Clause(where_cl, from_cl, racine);
        }

        //racine.print(System.out, racine);
        initPlan = racine;
    }

    Noeud Select_Clause(String clause) {
        String[] cols = clause.split("\\s*,\\s*");
        int i = 0;
        String cnt = "Project(";
        if (cols.length == 1 && cols[i].equals("*")) {
            return null;
        } else {
            while (i < cols.length - 1) {
                //we keep the loop here because we might proceed to extra processing for columns later (Ex: DYSTINCT, COUNT, alias, ect...)
                if ((cols[i].split("[.]")).length == 2) {
                    cnt = cnt.concat((cols[i].split("[.]"))[1] + ",");
                } else {
                    cnt = cnt.concat(cols[i] + ",");
                }
                i++;
            }
            if ((cols[i].split("[.]")).length == 2) {
                cnt = cnt.concat((cols[i].split("[.]"))[1] + ")");
            } else {
                cnt = cnt.concat(cols[i] + ")");
            }
            return new Noeud(new Content(cnt));
        }
    }

    Noeud From_Clause(String clause) {
        String[] relations = clause.split("\\s*,\\s*");
        int i = 0;
        Noeud noeud = null;
        ///traitement du clause from
        for (String relation : relations) {
            String alias = new String();
            if (relation.split(" ").length == 2) {
                alias = relation.split(" ")[1];
                relation = relation.split(" ")[0];
            }
            if (Objects.isNull(noeud)) {
                noeud = new Noeud(new Content(relation));
            } else {
                Noeud join = new Noeud(new Content("Join"), noeud, new Noeud(new Content(relation)));
                noeud = join;
            }
        }
        return noeud;
    }

    Noeud Where_Clause(String where_cl, String from_cl, Noeud racine) {
        String[] relations = from_cl.split("\\s*,\\s*");
        int i = 0;
        //update the alias in where clause with table names
        for (String relation : relations) {
            String alias = new String();
            if (relation.split(" ").length == 2) {
                alias = relation.split(" ")[1];
                relation = relation.split(" ")[0];
               
                where_cl = remplacerAlias(where_cl, relation, alias);
            }
        }
        //create noeuds
        String[] words = where_cl.split("\\s+");
        i = 0;
        while (i < words.length) {
            String cond = new String();
            String head_op = new String();
            while (i < words.length) {
                if (words[i].equals("and") || words[i].equals("or")) {
                    head_op = words[i];
                    break;
                }
                cond = cond.concat(words[i]);
                i++;
            }
            //T1.a>T2.b
            ///{T1.a, 100}
            String[] operands = cond.split("[<>!]=|[=><]");
            ///T1
            String table1 = (operands[0].split("[.]"))[0];
            ///T2
            String table2 = new String();
            if (operands[1].indexOf(".") > 0) {
                table2 = (operands[1].split("[.]"))[0];
            } else {
                table2 = operands[1];
            }
            ///a
            String[] field = operands[0].split("[.]");
            String op1 = field[1];
            String op2 = new String();
            ///b
            if (operands[1].split("[.]").length == 2) {
                op2 = (operands[1].split("[.]"))[1];
            } ///si il s'agit d'une constante
            else {
                op2 = operands[1];
            }
            ///operateur:>
            String o = cond.substring(operands[0].length(), cond.length() - operands[1].length());
            if (Pattern.matches("[0-9]+", operands[1]) || Pattern.matches("(')[a-zA-Z0-9]+(')", operands[1]) || table1.equals(table2)) {
                racine = insererSelection("Selection(" + op1 + o + op2 + ")", table1, racine);
            } else {
                //condition on two columns in same table
                if (o.equals("=")) {
                    racine = insererJointure(op1 + o + op2, table1, table2, racine, false);
                } else {
                    racine = insererJointure("Selection(" + op1 + o + op2 + ")", table1, table2, racine, true);
                }
            }
            i++;
        }
        return racine;
    }

    public Noeud insererSelection(String cond, String table, Noeud racine) {
        if (racine.feuille && racine.content.getData().equals(table)) {
            Noeud selection = new Noeud(new Content(cond), racine, null);
            racine = selection;
        } else {
            if (Objects.nonNull(racine.gauche)) {
                racine.gauche = insererSelection(cond, table, racine.gauche);
            }
            if (Objects.nonNull(racine.droit)) {
                racine.droit = insererSelection(cond, table, racine.droit);
            }
        }
        return racine;
    }

    public Noeud insererJointure(String cond, String table1, String table2, Noeud racine, boolean selection) {
        if (racine.content.getData().equals("Join") && ((recherche_relation(table1, racine.gauche) && recherche_relation(table2, racine.droit))
                || (recherche_relation(table2, racine.gauche) && recherche_relation(table1, racine.droit)))) {
            if (selection) {
                Noeud noeud = new Noeud(new Content(cond), racine, null);
                racine = noeud;
            } else {
                racine.content.setData(racine.content.getData() + "(" + cond + ")");
            }
        } else {
            if (Objects.nonNull(racine.gauche)) {
                racine.gauche = insererJointure(cond, table1, table2, racine.gauche, selection);
            }

            if (Objects.nonNull(racine.droit)) {
                racine.droit = insererJointure(cond, table1, table2, racine.droit, selection);
            }

        }

        return racine;
    }

    public boolean recherche_relation(String word, Noeud root) {
        // parcour d'arbre
        if (root.content.getData().equals(word)) {
            return true;
        }
        if (root.gauche != null) {
            if (recherche_relation(word, root.gauche)) {
                return true;
            }
        }
        if (root.droit != null) {
            if (recherche_relation(word, root.droit)) {
                return true;
            }
        }
        return false;

    }

    //caractère avant: !!!
    String remplacerAlias(String clause, String table, String alias) {
        String a = alias.concat(".");
        return clause.replaceAll("\\b"+a+"\\b", table.concat("."));
    }

}
