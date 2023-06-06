/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.bo.Noeud;

/**
 *
 * @author H4NANE
 */
public class CatalogueService {
    public static String file = "stats.txt";
    public static double[] initiate_stats() {
        double[] stats=null;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            stats = new double[6];
            int i = 0;
            String line;
            while ((line = br.readLine()) != null) {
                stats[i] = Double.parseDouble(line);
                if (i++ == 5) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    public static double getNt(Noeud noeud) {
        if (noeud.content.getData().indexOf("Join") == 0) {
            if (noeud.content.getData().equals("Join")) {
                ///natural join later
                return getNt(noeud.gauche) * getNt(noeud.droit);
            } else {
                return inner_join_Nt(noeud);
            }
        } else if (noeud.content.getData().indexOf("Selection") == 0) {
            return getNt_Selection(noeud);
        } else if (noeud.content.getData().indexOf("Project") == 0) {
            return getNt(noeud.gauche);
        } else if (noeud.gauche == null && noeud.droit == null) {
            return getNbLignes(noeud.content.getData());
        }
        return -1;
    }
    
    public static int getNbLignes(String table) {
        try (BufferedReader br = new BufferedReader(new FileReader("stats.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("[" + table.toUpperCase() + "]")) {
                    String[] parts = line.split("[:]");
                    return Integer.parseInt(parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static double getNt_Selection(Noeud noeud) {
        String cond = noeud.content.getData();
        String[] operands = cond.substring(cond.indexOf("(") + 1, cond.indexOf(")")).split("[=]|[!=]|[>]|[>=]|[<]|[<=]");
        return getNt(noeud.gauche) / getCard(noeud.gauche, operands[0]);
    }

    public static int getCard(Noeud noeud, String col) {
        if (noeud.content.getData().indexOf("Selection") == 0) {
            return getCard(noeud.gauche, col);
        }
        if (noeud.content.getData().indexOf("Join") == 0) {
            int card = getCard(noeud.gauche, col);
            if (card > 0) {
                return card;
            } else {
                return getCard(noeud.droit, col);
            }
        } else if (noeud.gauche == null && noeud.droit == null) {
            try (BufferedReader br = new BufferedReader(new FileReader("stats.txt"))) {
                String line;
                Pattern pattern = Pattern.compile("\\{" + col.toUpperCase() + ";(\\d+);\\d+\\}");
                while ((line = br.readLine()) != null) {
                    if (line.indexOf("[" + noeud.content.getData().toUpperCase() + "]") == 0) {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            return Integer.parseInt(matcher.group(1));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }
        return -1;
    }

    public static double inner_join_Nt(Noeud noeud) {
        String cond = noeud.content.getData();
        String[] operands = cond.substring(cond.indexOf("(") + 1, cond.indexOf(")")).split("[=]");

        if (getNt_Join(noeud.gauche, operands[0], 1) > 0 && getNt_Join(noeud.droit, operands[1], 2) > 0) {
            return getNt_Join(noeud.gauche, operands[0], 1);
        } else if (getNt_Join(noeud.droit, operands[0], 1) > 0 && getNt_Join(noeud.gauche, operands[1], 2) > 0) {
            return getNt_Join(noeud.droit, operands[0], 1);
        } else if (getNt_Join(noeud.gauche, operands[1], 1) > 0 && getNt_Join(noeud.droit, operands[0], 2) > 0) {
            return getNt_Join(noeud.gauche, operands[1], 1);
        } else if(getNt_Join(noeud.droit, operands[1], 1)>0){
            return getNt_Join(noeud.droit, operands[1], 1);
        }
        //produit cartesian
        else return getNt(noeud.gauche)*getNt(noeud.droit);

    }

    public static double getNt_Join(Noeud noeud, String col, int index) {
        if (noeud.content.getData().indexOf("Selection") == 0) {
            if (noeud.gauche.gauche == null && noeud.gauche.droit == null) {
                try (BufferedReader br = new BufferedReader(new FileReader("stats.txt"))) {
                    String line;
                    Pattern pattern = Pattern.compile("\\{" + col.toUpperCase() + ";\\d+;(\\d)\\}");
                    while ((line = br.readLine()) != null) {
                        if (line.indexOf("[" + noeud.gauche.content.getData().toUpperCase() + "]") == 0) {
                            Matcher matcher = pattern.matcher(line);
                            if (matcher.find()) {
                                if (Integer.parseInt(matcher.group(1)) == index) {
                                    return getNt(noeud);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return -1;
            } else {
                return getNt_Join(noeud.gauche, col, index);
            }
        }
        if (noeud.content.getData().indexOf("Join") == 0) {
            double nt = getNt_Join(noeud.gauche, col, index);
            if (nt > 0) {
                return nt;
            } else {
                return getNt_Join(noeud.droit, col, index);
            }
        } else if (noeud.gauche == null && noeud.droit == null) {
            try (BufferedReader br = new BufferedReader(new FileReader("stats.txt"))) {
                String line;
                Pattern pattern = Pattern.compile("\\{" + col.toUpperCase() + ";\\d+;(\\d)\\}");
                while ((line = br.readLine()) != null) {
                    if (line.indexOf("[" + noeud.content.getData().toUpperCase() + "]") == 0) {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            if (Integer.parseInt(matcher.group(1)) == index) {
                                String[] data = line.split("[:]");
                                return Integer.parseInt(data[1]);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }
        return -1;
    }
    
    

    public static String getRelationIndexed(Noeud noeud, String col, int index) {
        if (noeud.content.getData().indexOf("Selection") == 0) {
            if (noeud.gauche.gauche == null && noeud.gauche.droit == null) {
                try (BufferedReader br = new BufferedReader(new FileReader("stats.txt"))) {
                    String line;
                    Pattern pattern = Pattern.compile("\\{" + col.toUpperCase() + ";\\d+;(\\d)\\}");
                    while ((line = br.readLine()) != null) {
                        if (line.indexOf("[" + noeud.gauche.content.getData().toUpperCase() + "]") == 0) {
                            Matcher matcher = pattern.matcher(line);
                            if (matcher.find()) {
                                if (Integer.parseInt(matcher.group(1)) == index) {
                                    return noeud.gauche.content.getData().toUpperCase();
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            } else {
                return getRelationIndexed(noeud.gauche, col, index);
            }
        }
        if (noeud.content.getData().indexOf("Join") == 0) {
            String name = getRelationIndexed(noeud.gauche, col, index);
            if (name.length() > 0) {
                return name;
            } else {
                return getRelationIndexed(noeud.droit, col, index);
            }
        } else if (noeud.gauche == null && noeud.droit == null) {
            try (BufferedReader br = new BufferedReader(new FileReader("stats.txt"))) {
                String line;
                Pattern pattern = Pattern.compile("\\{" + col.toUpperCase() + ";\\d+;(\\d)\\}");
                while ((line = br.readLine()) != null) {
                    if (line.indexOf("[" + noeud.content.getData().toUpperCase() + "]") == 0) {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            if (Integer.parseInt(matcher.group(1)) == index) {
                                String[] data = line.split("[:]");
                                return noeud.content.getData().toUpperCase();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
        return "";
    }
}
