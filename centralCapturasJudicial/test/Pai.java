

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author f8940147
 */
@Entity
@Table(name="tab_pai")
public class Pai implements Serializable {

    @Id    
    @Column(name="id")
    private int id;
    @Column(name="nome")
    private String nome;
    @Column(name="cpf")
    private String cpf;    
//    @OneToMany(mappedBy = "pai", cascade=CascadeType.ALL)
//    private List<Filho> filhos;

    public Pai() {
    }    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

//    public List<Filho> getFilhos() {
//        return filhos;
//    }
//
//    public void setFilhos(List<Filho> filhos) {
//        this.filhos = filhos;
//    }

}
