/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bo;

import java.util.*;
import view.OptimizerForm;

/**
 *
 * @author H4NANE
 */
//select e.employee_id from employees e, departments d, jobs j where e.employee_id = d.manager_id and e.job_id = j.job_id;
//select e.employee_id from employees e, departments d, jobs j where e.employee_id = d.manager_id and e.job_id = j.job_id;
//select e.employee_id from employees e, departments d, jobs j, locations l where e.employee_id = d.manager_id and e.job_id = j.job_id and l.location_id=d.location_id and e.salary>5000 and d.department_name='xxx';
//
public class Transformer {

    private HashMap<Noeud, List<Noeud>> plans;
    private List<Noeud> plans_log;
    private List<List<Noeud>> plans_phy;
    private List<String> regles;
    private Builder builder;

    public Transformer(Builder builder) {
        this.builder = builder;
        this.plans = new HashMap<Noeud, List<Noeud>>();
        this.plans_log = new ArrayList<Noeud>();
        this.regles = new ArrayList<String>();
        this.plans_phy = new ArrayList<List<Noeud>>();
        regles.add("");
        this.plans_log.add(builder.getInitPlan());
        this.plans.put(builder.getInitPlan(), new ArrayList<Noeud>());
    }

    public List<Noeud> getPlans_log() {
        return plans_log;
    }

    public List<List<Noeud>> getPlans_phy() {
        return plans_phy;
    }

    public List<String> getRegles() {
        return regles;
    }
    
    

    public void translateTreeLog() {
        Noeud dup = new Noeud(plans_log.get(0));
        applyCSJ(dup, dup);
        ArrayList<Noeud> dupList = new ArrayList<Noeud>(plans_log);
        for (int i = 0; i < dupList.size(); i++) {
            Noeud dup2 = new Noeud(dupList.get(i));
            applySC(dup2, dup2, i);
        }
        dupList = new ArrayList<Noeud>(plans_log);
        for (int i = 0; i < dupList.size(); i++) {
            Noeud dup2 = new Noeud(dupList.get(i));
            applySE(dup2, dup2, i);
        }
        dupList = new ArrayList<Noeud>(plans_log);
        for (int i = 0; i < dupList.size(); i++) {
            Noeud dup2 = new Noeud(dupList.get(i));
            applyJC(dup2, dup2, i);
        }
        dupList = new ArrayList<Noeud>(plans_log);
        for (int i = 0; i < dupList.size(); i++) {
            Noeud dup2 = new Noeud(dupList.get(i));
            applyJA(dup2, dup2, i);
        }
        int i = 1;
        /*for (Noeud plan : plans_log) {
            System.out.println("--> Regles ");
            System.out.println(regles.get(i - 1));
            System.out.println("--> Plan " + i++);
            plan.print(System.out, plan);
        }*/
    }

    public Noeud JC(Noeud noeud, Noeud racine, int org_plan_index) {
        if (noeud.content.getData().indexOf("Join") == 0) {
            regles.add(regles.get(org_plan_index) + "-Comutativité de la jointure entre " + noeud.gauche.content.getData() + " et " + noeud.droit.content.getData() + ".\n");
            Noeud tmp = noeud.gauche;
            noeud.gauche = noeud.droit;
            noeud.droit = tmp;
            plans.put(new Noeud(racine), new ArrayList<Noeud>());
            plans_log.add(new Noeud(racine));
        }
        if (noeud.gauche != null) {
            noeud.gauche = JC(noeud.gauche, racine, org_plan_index);
        }
        if (noeud.droit != null) {
            noeud.droit = JC(noeud.droit, racine, org_plan_index);
        }
        return noeud;
    }

    public void applyJC(Noeud noeud, Noeud racine, int org_plan_index) {
        if (noeud.gauche != null) {
            applyJC(noeud.gauche, racine, org_plan_index);
        }
        if (noeud.droit != null) {
            applyJC(noeud.droit, racine, org_plan_index);
        }
        if (noeud.content.getData().indexOf("Join") == 0) {
            JC(noeud, racine, org_plan_index);
        }
    }

    public Noeud JA(Noeud noeud, Noeud racine, int org_plan_index) {
        if (noeud.content.getData().indexOf("Join") == 0) {
            Noeud dup = new Noeud(noeud);
            if (noeud.gauche != null && noeud.gauche.content.getData().indexOf("Join") == 0) {
                String content = noeud.content.getData();
                noeud.content.setData(noeud.gauche.content.getData());
                noeud.gauche.content.setData(content);
                regles.add(regles.get(org_plan_index) + "-Associétivité de la jointure entre " + noeud.content.getData() + " et " + noeud.gauche.content.getData() + ".\n");
                Noeud tmp = noeud.gauche.gauche;
                noeud.gauche.gauche = noeud.gauche.droit;
                noeud.gauche.droit = noeud.droit;
                noeud.droit = noeud.gauche;
                noeud.gauche = tmp;
                plans.put(new Noeud(racine), new ArrayList<Noeud>());
                plans_log.add(new Noeud(racine));
            }
            noeud = dup;
            //exceptions
            /*if(noeud.droit!=null && noeud.droit.content.getData().indexOf("Join") == 0){
                Noeud tmp = noeud.droit.droit;
                noeud.droit.droit = noeud.droit.gauche;
                noeud.droit.gauche = noeud.gauche;
                noeud.gauche = noeud.droit;
                noeud.droit = tmp;
                plans.add(new Noeud(racine));
            }*/
        }
        if (noeud.gauche != null) {
            noeud.gauche = JA(noeud.gauche, racine, org_plan_index);
        }
        if (noeud.droit != null) {
            noeud.droit = JA(noeud.droit, racine, org_plan_index);
        }
        return noeud;
    }

    public void applyJA(Noeud noeud, Noeud racine, int org_plan_index) {
        if (noeud.gauche != null) {
            applyJA(noeud.gauche, racine, org_plan_index);
        }
        if (noeud.droit != null) {
            applyJA(noeud.droit, racine, org_plan_index);
        }
        if (noeud.content.getData().indexOf("Join") == 0) {
            JA(noeud, racine, org_plan_index);
        }
    }

    public Noeud CSJ(Noeud noeud, Noeud racine) {
        if (noeud.gauche != null && noeud.gauche.content.getData().indexOf("Join") == 0) {
            if (noeud.gauche.gauche != null && noeud.gauche.gauche.content.getData().indexOf("Selection") == 0) {
                regles.add(regles.get(regles.size() - 1) + "-Comutativité de la sélection et de jointure entre " + noeud.gauche.content.getData() + " et " + noeud.gauche.gauche.content.getData() + ".\n");
                Noeud tmp = noeud.gauche.gauche;
                noeud.gauche.gauche = noeud.gauche.gauche.gauche;
                tmp.gauche = noeud.gauche;
                noeud.gauche = tmp;
                plans_log.add(new Noeud(racine));
            }
            if (noeud.gauche.droit != null && noeud.gauche.droit.content.getData().indexOf("Selection") == 0) {
                regles.add(regles.get(regles.size() - 1) + "-Comutativité de la sélection et de jointure entre " + noeud.gauche.content.getData() + " et " + noeud.gauche.droit.content.getData() + ".\n");
                Noeud tmp = noeud.gauche.droit;
                noeud.gauche.droit = noeud.gauche.droit.gauche;
                tmp.gauche = noeud.gauche;
                noeud.gauche = tmp;
                plans_log.add(new Noeud(racine));
            }
        }
        if (noeud.droit != null && noeud.droit.content.getData().indexOf("Join") == 0) {
            if (noeud.droit.gauche != null && noeud.droit.gauche.content.getData().indexOf("Selection") == 0) {
                regles.add(regles.get(regles.size() - 1) + "-Comutativité de la sélection et de jointure entre " + noeud.droit.content.getData() + " et " + noeud.droit.gauche.content.getData() + ".\n");
                Noeud tmp = noeud.droit.gauche;
                noeud.droit.gauche = noeud.droit.gauche.gauche;
                tmp.gauche = noeud.droit;
                noeud.droit = tmp;
                plans_log.add(new Noeud(racine));
            }
            if (noeud.droit.droit != null && noeud.droit.droit.content.getData().indexOf("Selection") == 0) {
                regles.add(regles.get(regles.size() - 1) + "-Comutativité de la sélection et de jointure entre " + noeud.droit.content.getData() + " et " + noeud.droit.droit.content.getData() + ".\n");
                Noeud tmp = noeud.droit.droit;
                noeud.droit.droit = noeud.droit.droit.gauche;
                tmp.gauche = noeud.droit;
                noeud.droit = tmp;
                plans_log.add(new Noeud(racine));
            }
        }
        if (noeud.gauche != null) {
            noeud.gauche = CSJ(noeud.gauche, racine);
        }
        if (noeud.droit != null) {
            noeud.droit = CSJ(noeud.droit, racine);
        }
        return noeud;
    }

    public void applyCSJ(Noeud noeud, Noeud racine) {
        if (noeud.gauche != null) {
            applyCSJ(noeud.gauche, racine);
        }
        if (noeud.droit != null) {
            applyCSJ(noeud.droit, racine);
        }
        CSJ(noeud, racine);
    }

    public Noeud SE(Noeud noeud, Noeud racine, int org_plan_index) {
        if (noeud.content.getData().indexOf("Selection") == 0) {
            if (noeud.gauche != null && noeud.gauche.content.getData().indexOf("Selection") == 0) {
                regles.add(regles.get(org_plan_index) + "-Eclatement de la sélection conjuctive entre " + noeud.content.getData() + " et " + noeud.gauche.content.getData() + ".\n");
                String cond1 = noeud.content.getData().substring(noeud.content.getData().indexOf("(") + 1, noeud.content.getData().indexOf(")"));
                String cond2 = noeud.gauche.content.getData().substring(noeud.gauche.content.getData().indexOf("(") + 1, noeud.gauche.content.getData().indexOf(")"));
                String cnt = "Selection(" + cond1 + " AND " + cond2 + ")";
                noeud.content.setData(cnt);
                noeud.gauche = noeud.gauche.gauche;
                plans_log.add(new Noeud(racine));
            }
        }
        if (noeud.gauche != null) {
            noeud.gauche = SE(noeud.gauche, racine, org_plan_index);
        }
        if (noeud.droit != null) {
            noeud.droit = SE(noeud.droit, racine, org_plan_index);
        }
        return noeud;
    }

    public void applySE(Noeud noeud, Noeud racine, int org_plan_index) {
        if (noeud.gauche != null) {
            applySE(noeud.gauche, racine, org_plan_index);
        }
        if (noeud.droit != null) {
            applySE(noeud.droit, racine, org_plan_index);
        }
        SE(noeud, racine, org_plan_index);
    }

    public Noeud SC(Noeud noeud, Noeud racine, int org_plan_index) {
        if (noeud.gauche != null && noeud.gauche.content.getData().indexOf("Selection") == 0) {
            if (noeud.gauche.gauche != null && noeud.gauche.gauche.content.getData().indexOf("Selection") == 0) {
                regles.add(regles.get(org_plan_index) + "-Comutativité de la sélection entre " + noeud.gauche.content.getData() + " et " + noeud.gauche.gauche.content.getData() + ".\n");
                Noeud tmp = noeud.gauche;
                noeud.gauche = noeud.gauche.gauche;
                tmp.gauche = tmp.gauche.gauche;
                noeud.gauche.gauche = tmp;
                plans_log.add(new Noeud(racine));
            }
        }
        if (noeud.droit != null && noeud.droit.content.getData().indexOf("Selection") == 0) {
            if (noeud.droit.gauche != null && noeud.droit.gauche.content.getData().indexOf("Selection") == 0) {
                regles.add(regles.get(org_plan_index) + "-Comutativité de la sélection entre " + noeud.droit.content.getData() + " et " + noeud.droit.gauche.content.getData() + ".\n");
                Noeud tmp = noeud.droit;
                noeud.droit = noeud.droit.gauche;
                tmp.gauche = tmp.gauche.gauche;
                noeud.droit.gauche = tmp;
                plans_log.add(new Noeud(racine));
            }
        }
        if (noeud.gauche != null) {
            noeud.gauche = SC(noeud.gauche, racine, org_plan_index);
        }
        if (noeud.droit != null) {
            noeud.droit = SC(noeud.droit, racine, org_plan_index);
        }
        return noeud;
    }

    public void applySC(Noeud noeud, Noeud racine, int org_plan_index) {
        if (noeud.gauche != null) {
            applySC(noeud.gauche, racine, org_plan_index);
        }
        if (noeud.droit != null) {
            applySC(noeud.droit, racine, org_plan_index);
        }
        if ((noeud.gauche != null && noeud.gauche.content.getData().indexOf("Selection") == 0)
                || (noeud.droit != null && noeud.droit.content.getData().indexOf("Selection") == 0)) {
            SC(noeud, racine, org_plan_index);
        }
    }

    public void translateTreePhy() {
        translateTreeLog();
        for (int i = 0; i < plans_log.size(); i++) {
            Estimator E = new Estimator(plans_log.get(i));
            E.run();
            plans_phy.add(E.getPhyPlans());
        }

        int i = 0;
        /*
        for (List<Noeud> p : plans_phy) {
            System.out.println("--> Plan Log :" + i);
            plans_log.get(i).print(System.out, plans_log.get(i++));
            for (Noeud pp : p) {
                System.out.println("--> Plan Phy : ");
                pp.print(System.out, pp);
                System.out.println("Mat: " + pp.exec_Materialization());
                System.out.println("Pipe: " + pp.exec_Pipeline(pp.cout, 0));
            }
        }*/

        Optimizer Opt = new Optimizer(plans_log, plans_phy);
        regles.set(0, "Plan Initial.");
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String query = new String();
                for (Noeud plan : Opt.getOpt_plan_pipe().keySet()) {
                    query = Opt.rewrite_Query(plan, "");
                }
                new OptimizerForm(plans_log, plans_phy, regles, Opt.getOpt_plan_mat(), Opt.getOpt_plan_pipe(), query).setVisible(true);
            }
        });
    }
}
