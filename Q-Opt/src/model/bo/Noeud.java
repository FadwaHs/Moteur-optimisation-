/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bo;

/**
 *
 * @author H4NANE
 */
import java.io.PrintStream;

/**
 *
 * @author H4NANE
 */
public class Noeud {

    public Noeud gauche;
    public Noeud droit;
    public Content content;
    public boolean feuille; //false
    public double cout=0.0;

    public Noeud(Content ct) {
        this.content = ct;
        this.gauche = null;
        this.droit = null;
        this.feuille = true;
    }

    public Noeud(Content ct, Noeud fg, Noeud fd) {
        this.content = ct;
        this.gauche = fg;
        this.droit = fd;
        this.feuille = false;
    }

    public Noeud(Noeud racine){
        if(racine != null) content = new Content(racine.content.getData());
        if(racine.gauche!=null) this.gauche = new Noeud(racine.gauche);
        if(racine.droit!=null) this.droit = new Noeud(racine.droit);
    }

    public void setCout(double cout) {
        this.cout = cout;
    }

    public double getCout() {
        return cout;
    }

    // affichage : 
    public void Affichage_Infixe(Noeud root) {
        System.out.print(" " + root.content.getData());
        if (root.gauche != null) {
            Affichage_Infixe(root.gauche);
        }
        if (root.droit != null) {
            Affichage_Infixe(root.droit);
        }
    }

    public void traversePreOrder(StringBuilder sb, String padding, String pointer, Noeud root) {
        if (root != null) {

            sb.append(padding);
            sb.append(pointer);
            sb.append(root.content.getData()); //root
            sb.append("\n");

            StringBuilder paddingBuilder = new StringBuilder(padding);
            paddingBuilder.append("| ");

            String paddingForBoth = paddingBuilder.toString();
            String pointerForRight = "R+----";
            String pointerForLeft = (root.droit != null) ? "L|----" : "L+----";

            traversePreOrder(sb, paddingForBoth, pointerForLeft, root.gauche);
            traversePreOrder(sb, paddingForBoth, pointerForRight, root.droit);

        }
    }

    public void print(PrintStream os, Noeud root) {

        StringBuilder sb = new StringBuilder();
        traversePreOrder(sb, "", "", root);
        os.print(sb.toString());
    }
    
    public double calculerCout(){
        if(gauche!=null && droit!=null) return cout+gauche.calculerCout()+droit.calculerCout();
        else if(gauche!=null && droit==null) return cout+gauche.calculerCout();
        else if(gauche==null && droit!=null) return cout+droit.calculerCout();
        else return cout;
    }
    
    public double exec_Materialization(){
        if(gauche!=null && droit!=null) return cout+gauche.exec_Materialization()+droit.exec_Materialization()+1.1;
        if(gauche!=null && content.getData().indexOf("Project")==0) return cout+gauche.exec_Materialization();
        if(gauche!=null) return cout+gauche.exec_Materialization()+1.1;
        else return cout;
    }

    public double exec_Pipeline(Double cout, int pos){
        if(gauche!=null && droit!=null) {
            if(pos==0) return max(gauche.exec_Pipeline(this.cout,pos+1),droit.exec_Pipeline(this.cout,pos+1));
            else{
                if(max(this.cout, cout)==this.cout) return max(gauche.exec_Pipeline(this.cout+1,pos+1),droit.exec_Pipeline(this.cout+1,pos+1)); 
                else return max(gauche.exec_Pipeline(cout,pos+1),droit.exec_Pipeline(cout,pos+1)); 
            }
        }
        if(gauche!=null) return gauche.exec_Pipeline(cout,pos);
        else return cout;
    }

    public double max(double a, double b){
        if(a>=b) return a;
        return b;
    }
    
}
