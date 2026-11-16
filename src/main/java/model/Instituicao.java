package model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;


@Entity
public class Instituicao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "nome_instituicao")
    private String nome;
    @Column(name = "email_instituicao", unique = true)
    private String email;
    @OneToMany(mappedBy = "instituicao")
    @OrderBy("id ASC")
    private List<Denuncia> denuncias = new ArrayList<>();


    public Instituicao() { }

    public Instituicao(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public int getId() {
    	return id;
    }
    
    public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Denuncia> getDenuncias() {
		return denuncias;
	}
	
	@Override
    public String toString() {
        return id + " - " + nome + " | " + email;
    }
}
