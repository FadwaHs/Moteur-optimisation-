/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cotroller;

import model.bo.Builder;
import model.bo.Transformer;

/**
 *
 * @author H4NANE
 */
public class Moteur {
    public void Optimiser(String body){
        Builder builder = new Builder(body);
        builder.createInitPlan();
        Transformer transformer = new Transformer(builder);
        transformer.translateTreePhy();
    }
    
    public Transformer transformerRequete(String body){
        Builder builder = new Builder(body);
        builder.createInitPlan();
        return new Transformer(builder);
    }
}
