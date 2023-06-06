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
public class Content {
    private String data;

    public Content(String data) {
        this.data = data;
    }
    
    public String getData(){
        return data;
    }
    
    public void setData(String data){
        this.data = data;
    }
}