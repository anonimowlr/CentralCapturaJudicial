/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author f8940147
 */
@Entity
@Table(name="tab_filho")
public class Filho implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="codigo")
    private int codigo;
    @Column
    private String nome;
    @ManyToOne//(cascade=CascadeType.ALL)
    @JoinColumn(name="pai_id")//, referencedColumnName="mci")
    private Pai pai;

    public Filho() {
    }    
   
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    
    public Pai getPai() {
        return pai;
    }

    public void setCliente(Pai pai) {
        this.pai = pai;
    }

}
