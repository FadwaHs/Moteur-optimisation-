/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 *
 * @author H4NANE
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import model.bo.Noeud;

/**
 *
 * @author fadwa
 */
public class Drawer extends JPanel {

    private Noeud racine;
    private int width;
    private int height;

    public Drawer(Noeud racine) {
        this.racine = racine;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(253, 253, 253));
        width = getWidth();
        height = getHeight();
        afficherNoeud(g, racine, width / 2, 20, 80);
    }

    private void afficherNoeud(Graphics g, Noeud n, int x, int y, int espace) {

        g.setColor(Color.BLACK);
        ArrayList<String> content = this.to_Unicode(n);

        if (content.get(1) == null) {
            if(n.gauche!=null) g.setColor(new Color(61, 131, 214));
            else g.setColor(Color.BLACK);
            g.setFont(new java.awt.Font("Times New Roman", 1, 16));
            g.drawString(content.get(0), x, y);
        } else {
            g.setFont(new java.awt.Font("TimesRoman", 0, 20));
            g.drawString(content.get(0), x, y);
            if (content.get(1) != null) {
                g.setFont(new java.awt.Font("Times New Roman", 1, 14));
                g.drawString(content.get(1), x + 20, y + 12);
            }
        }
        if (n.gauche != null && n.droit == null) {
            Graphics2D g2 = (Graphics2D) g;
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(2));
            g2.setFont(new java.awt.Font("Times New Roman", 3, 14));
            g2.setColor(Color.BLACK);
            g2.drawLine(x + 10, y + 20, x + 10, y + 80);
            afficherNoeud(g, n.gauche, x, y + 100, espace / 2);
        } else if (n.gauche != null && n.droit != null) {
            g.setFont(new java.awt.Font("Times New Roman", 3, 14));
            g.setColor(Color.BLACK);

            g.drawLine(x + 10, y + 20, x - 80, y + 80);
            afficherNoeud(g, n.gauche, x - 90, y + 100, espace / 2);
            g.drawLine(x + 10, y + 20, x + 80, y + 80);
            afficherNoeud(g, n.droit, x + 90, y + 100, espace / 2);
        }
    }

    public ArrayList<String> to_Unicode(Noeud racine) {
        ArrayList<String> content = new ArrayList<String>();
        if (racine.content.getData().contains("[")) {
            content.add(racine.content.getData().substring(racine.content.getData().indexOf("[") + 1, racine.content.getData().indexOf("]")));
            content.add(null);

        } else if (racine.content.getData().contains("(")) {
            content.add(racine.content.getData().substring(0, racine.content.getData().indexOf("(")));
            content.add(racine.content.getData().substring(racine.content.getData().indexOf("(") + 1, racine.content.getData().indexOf(")")));
        } else {
            content.add(racine.content.getData());
            content.add(null);
        }
        if (content.get(0).equals("Project")) {
            content.set(0, "\uD835\uDEB7");
        } else if (content.get(0).equals("Selection")) {
            content.set(0, "\uD835\uDED4");
        } else if (content.get(0).equals("Join")) {
            content.set(0, "\u2A1D");
        } else {
            content.set(0, String.valueOf(content.get(0).charAt(0)).toUpperCase().concat(content.get(0).substring(1)));
        }
        return content;
    }

}
