/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import model.service.CatalogueService;

/**
 *
 * @author H4NANE
 */
public class Estimator extends Thread {

    private Noeud plan;
    private List<Noeud> plans_phy;
    private String file = "stats.txt";
    private double[] stats;
    //0:facteur de balayage;
    //1:taille du bloc select distinct bytes/blocks from user_segments;
    //2:taille descripteur du bloc
    //3:temps trans
    //4:temps pos début
    //3+4:temps ES bloc tt+tpd
    //5:NombrePos

    public Estimator(Noeud plan) {
        this.plan = new Noeud(plan);
        stats = CatalogueService.initiate_stats();
        plans_phy = new ArrayList<Noeud>();
    }

    public List<Noeud> getPhyPlans() {
        return plans_phy;
    }

    public Noeud to_BIB(Noeud noeud) {
        if (noeud.gauche == null && noeud.droit == null) {
            noeud.setCout(0.0);
        }
        if (noeud.content.getData().indexOf("Selection") == 0 || noeud.content.getData().indexOf("Project") == 0) {
            noeud.setCout(BAL(noeud));
            noeud.content.setData(noeud.content.getData().concat("\n[BALAYAGE: " + new DecimalFormat("#.###").format(noeud.getCout()) + "ms]"));
        }
        if (noeud.content.getData().indexOf("Join") == 0) {
            noeud.setCout(BIB(noeud));
            noeud.content.setData(noeud.content.getData().concat("\n[BIB: " + new DecimalFormat("#.###").format(noeud.getCout()) + "ms]"));
        }
        if (noeud.gauche != null) {
            noeud.gauche = to_BIB(noeud.gauche);
        }
        if (noeud.droit != null) {
            noeud.droit = to_BIB(noeud.droit);
        }
        return noeud;
    }

    public Noeud to_BII(Noeud noeud) {
        if (noeud.gauche == null && noeud.droit == null) {
            noeud.setCout(0.0);
        }
        if (noeud.content.getData().indexOf("Selection") == 0 || noeud.content.getData().indexOf("Project") == 0) {
            noeud.setCout(BAL(noeud));
            noeud.content.setData(noeud.content.getData().concat("\n[BALAYAGE: " + new DecimalFormat("#.###").format(noeud.getCout()) + "ms]"));
        }
        if (noeud.content.getData().indexOf("Join") == 0) {
            noeud.setCout(BII(noeud));
            noeud.content.setData(noeud.content.getData().concat("\n[BII: " + new DecimalFormat("#.###").format(noeud.getCout()) + "ms]"));

        }
        if (noeud.gauche != null) {
            noeud.gauche = to_BII(noeud.gauche);
        }
        if (noeud.droit != null) {
            noeud.droit = to_BII(noeud.droit);
        }
        return noeud;
    }

    public Noeud to_JH(Noeud noeud) {
        if (noeud.gauche == null && noeud.droit == null) {
            noeud.setCout(0.0);
        }
        if (noeud.content.getData().indexOf("Selection") == 0 || noeud.content.getData().indexOf("Project") == 0) {
            noeud.setCout(BAL(noeud));
            noeud.content.setData(noeud.content.getData().concat("\n[BALAYAGE: " + new DecimalFormat("#.###").format(noeud.getCout()) + "ms]"));
        }
        if (noeud.content.getData().indexOf("Join") == 0) {
            noeud.setCout(JH(noeud));
            noeud.content.setData(noeud.content.getData().concat("\n[JH: " + new DecimalFormat("#.###").format(noeud.getCout()) + "ms]"));
        }
        if (noeud.gauche != null) {
            noeud.gauche = to_JH(noeud.gauche);
        }
        if (noeud.droit != null) {
            noeud.droit = to_JH(noeud.droit);
        }
        return noeud;
    }

    public Noeud to_PJ(Noeud noeud) {
        if (noeud.gauche == null && noeud.droit == null) {
            noeud.setCout(0.0);
        }
        if (noeud.content.getData().indexOf("Selection") == 0 || noeud.content.getData().indexOf("Project") == 0) {
            noeud.setCout(BAL(noeud));
            noeud.content.setData(noeud.content.getData().concat("\n[BALAYAGE: " + new DecimalFormat("#.###").format(noeud.getCout()) + "ms]"));
        }
        if (noeud.content.getData().indexOf("Join") == 0) {
            noeud.setCout(PJ(noeud));
            noeud.content.setData(noeud.content.getData().concat("\n[PJ: " + new DecimalFormat("#.###").format(noeud.getCout()) + "ms]"));
        }
        if (noeud.gauche != null) {
            noeud.gauche = to_PJ(noeud.gauche);
        }
        if (noeud.droit != null) {
            noeud.droit = to_PJ(noeud.droit);
        }
        return noeud;
    }

    public Noeud to_JTF(Noeud noeud) {
        if (noeud.gauche == null && noeud.droit == null) {
            noeud.setCout(0.0);
        }
        if (noeud.content.getData().indexOf("Selection") == 0 || noeud.content.getData().indexOf("Project") == 0) {
            noeud.setCout(BAL(noeud));
            noeud.content.setData(noeud.content.getData().concat("\n[BALAYAGE: " + new DecimalFormat("#.###").format(noeud.getCout()) + "ms]"));
        }
        if (noeud.content.getData().indexOf("Join") == 0) {
            noeud.setCout(JTF(noeud));
            noeud.content.setData(noeud.content.getData().concat("\n[JTF: " + new DecimalFormat("#.###").format(noeud.getCout()) + "ms]"));
        }
        if (noeud.gauche != null) {
            noeud.gauche = to_JTF(noeud.gauche);
        }
        if (noeud.droit != null) {
            noeud.droit = to_JTF(noeud.droit);
        }
        return noeud;
    }

    /*
    public boolean if_S_Indexed(Noeud noeud) {
        if (noeud.content.getData().indexOf("Join") == 0) {
            String cond = noeud.content.getData();
            String[] operands = cond.substring(cond.indexOf("(") + 1, cond.indexOf(")")).split("[=]");
            if (getNt_Join(noeud.droit, operands[0], 1) > 0 && getNt_Join(noeud.gauche, operands[1], 2) > 0) {
                return true;
            } else if (getNt_Join(noeud.droit, operands[1], 1) > 0 && getNt_Join(noeud.gauche, operands[0], 2) > 0) {
                return true;
            } else if (getNt_Join(noeud.droit, operands[0], 2) > 0 && getNt_Join(noeud.gauche, operands[1], 1) > 0) {
                return true;
            } else if (getNt_Join(noeud.droit, operands[1], 2) > 0 && getNt_Join(noeud.gauche, operands[0], 1) > 0) {
                return true;
            }
        }
        if (noeud.gauche != null) {
            if (if_S_Indexed(noeud.gauche)) {
                return true;
            }
        }
        if (noeud.droit != null) {
            if (if_S_Indexed(noeud.droit)) {
                return true;
            }
        }
        return false;
    }*/
    public double BAL(Noeud noeud) {
        return ((CatalogueService.getNt(noeud) / stats[0]) * stats[3]) + (stats[4] * stats[5]);
    }

    public double BIB(Noeud noeud) {
        return ((CatalogueService.getNt(noeud.gauche) / stats[0])) * ((stats[3] + stats[4]) + ((CatalogueService.getNt(noeud.droit) / stats[0]) * stats[3]) + stats[4]);
    }

    public double BII(Noeud noeud) {
        return ((CatalogueService.getNt(noeud.gauche) / stats[0]) * (stats[3] + stats[4])) + (CatalogueService.getNt(noeud.gauche) * ((getHauteur(noeud)) + 1) * (stats[3] + stats[4]));
    }

    public double JH(Noeud noeud) {
        return (BAL(noeud.gauche) + BAL(noeud.droit) + (2 * ((CatalogueService.getNt(noeud.gauche) / stats[0]) + (CatalogueService.getNt(noeud.droit) / stats[0])) * (stats[3] + stats[4])));
    }

    public double PJ(Noeud noeud) {
        return (BAL(noeud.gauche) + BAL(noeud.droit));
    }

    public double JTF(Noeud noeud) {
        return sort_T(noeud.gauche) + sort_T(noeud.droit) + (2 * ((CatalogueService.getNt(noeud.gauche) / stats[0]) + (CatalogueService.getNt(noeud.droit) / stats[0])) * (stats[3] + stats[4]));
    }

    public double sort_T(Noeud noeud) {
        return (CatalogueService.getNt(noeud) * Math.log(CatalogueService.getNt(noeud)) * 0.01);
    }

    public double getHauteur(Noeud noeud) {
        String cond = noeud.content.getData();
        String[] operands = cond.substring(cond.indexOf("(") + 1, cond.indexOf(")")).split("[=]");
        //10: OrdreMoyenT
        if (operands[0].equals(operands[1])) {
            double nt = CatalogueService.getNt_Join(noeud.droit, operands[0], 1);
            if (nt > 0) {
                return Math.log10(nt);
            } else {
                return Math.log10(CatalogueService.getNt_Join(noeud.droit, operands[0], 2));
            }
        } else {
            if (CatalogueService.getNt_Join(noeud.droit, operands[0], 1) > 0) {
                return Math.log10(CatalogueService.getNt_Join(noeud.droit, operands[0], 1));
            } else if (CatalogueService.getNt_Join(noeud.droit, operands[1], 1) > 0) {
                return Math.log10(CatalogueService.getNt_Join(noeud.droit, operands[1], 1));
            } else if (CatalogueService.getNt_Join(noeud.droit, operands[0], 2) > 0) {
                return Math.log10(CatalogueService.getNt_Join(noeud.droit, operands[0], 2));
            } else {
                return Math.log10(CatalogueService.getNt_Join(noeud.droit, operands[1], 2));
            }
        }
    }

    @Override
    public void run() {
        //test if there is a node with JOIN!!!!!
        plans_phy.add(to_BIB(new Noeud(plan)));
        plans_phy.add(to_BII(new Noeud(plan)));
        plans_phy.add(to_JH(new Noeud(plan)));
        plans_phy.add(to_PJ(new Noeud(plan)));
        plans_phy.add(to_JTF(new Noeud(plan)));
    }

}
