/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author H4NANE
 */
public class test {

    if (noeud.content.getData().indexOf("Join") == 0) {
            String cond = noeud.content.getData();
            String[] operands = cond.substring(cond.indexOf("(") + 1, cond.indexOf(")")).split("[=]");
            if (operands[0].equals(operands[1])) {
                if (getNt_Join(noeud.droit, operands[0]) > -1) {
                    return true;
                } else {
                    if (if_S_Indexed(noeud.gauche)) {
                        return true;
                    }
                    return if_S_Indexed(noeud.droit);
                }
            } else {
                if (getNt_Join(noeud.droit, operands[0]) > -1) {
                    return true;
                } else if (getNt_Join(noeud.droit, operands[1]) > -1) {
                    return true;
                } else {
                    if (if_S_Indexed(noeud.gauche)) {
                        return true;
                    }
                    return if_S_Indexed(noeud.droit);
                }
            }
        } else if (noeud.gauche != null) {
            if (if_S_Indexed(noeud.gauche)) {
                return true;
            } else if (noeud.droit != null) {
                return if_S_Indexed(noeud.droit);
            }
        }
        return false;
        
    public static void main(String[] args) {
        System.out.println(Math.log(1000000)/Math.log(66));
        //System.out.println("-->" + getNt_Selection(new Noeud(new Content("Selection(manager_id=5)"), new Noeud(new Content("departments"), null, null), null)));
    }

    public static double getNt(Noeud noeud) {
        if (noeud.content.getData().indexOf("Join") == 0) {
            if (noeud.content.getData().equals("Join")) {
                ///natural join later
                return getNt(noeud.gauche) * getNt(noeud.droit);
            } else {
                //fatna();
            }
        } else if (noeud.content.getData().indexOf("Selection") == 0) {
            return getNt_Selection(noeud);
        } else if (noeud.gauche==null && noeud.droit==null) {
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
        String[] operands = cond.substring(cond.indexOf("(")+1, cond.indexOf(")")).split("[=]|[!=]|[>]|[>=]|[<]|[<=]");
        return (double)(getNt(noeud.gauche) / getCard(noeud.gauche, operands[0]));
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
        } else if (noeud.gauche==null && noeud.droit==null) {
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

    public static int inner_join_Nt(Noeud noeud) {
        String cond = noeud.content.getData();
        String[] operands = cond.substring(cond.indexOf("[") + 1, cond.indexOf("]")).split("[=]");
        System.out.println(operands[0] + " - " + operands[1]);
        if (operands[0].equals(operands[1])) {
            return getNt_Join(noeud.gauche, operands[0]);
        } else {
            if (getNt_Join(noeud.gauche, operands[0]) > 0) {
                return getNt_Join(noeud.gauche, operands[0]);
            } else if (getNt_Join(noeud.droit, operands[0]) > 0) {
                return getNt_Join(noeud.droit, operands[0]);
            } else if (getNt_Join(noeud.gauche, operands[1]) > 0) {
                return getNt_Join(noeud.gauche, operands[1]);
            } else {
                return getNt_Join(noeud.droit, operands[1]);
            }
        }
    }

    public static int getNt_Join(Noeud noeud, String col) {
        System.out.println(noeud.content.getData() + " / " + col);
        if (noeud.content.getData().indexOf("Selection") == 0) {
            return getNt_Join(noeud.gauche, col);
        }
        if (noeud.content.getData().indexOf("Join") == 0) {
            int nt = getNt_Join(noeud.gauche, col);
            if (nt > 0) {
                return nt;
            } else {
                return getNt_Join(noeud.droit, col);
            }
        } else if (noeud.gauche == null && noeud.droit == null) {
            try (BufferedReader br = new BufferedReader(new FileReader("stats.txt"))) {
                String line;
                Pattern pattern = Pattern.compile("\\{" + col.toUpperCase() + ";\\d+;(\\d)\\}");
                while ((line = br.readLine()) != null) {
                    if (line.indexOf("[" + noeud.content.getData().toUpperCase() + "]") == 0) {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {

                            System.out.println(noeud.content.getData() + " || " + col);
                            if (Integer.parseInt(matcher.group(1)) == 1) {
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

}
