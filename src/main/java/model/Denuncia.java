package model;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
public class Denuncia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "titulo_denuncia")
	private String titulo;
	@Column(name = "descricao_denuncia")
	private String descricao;
	@Column(name = "local_denuncia")
	private String local;
	@Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
	@ManyToOne
	@JoinColumn(name = "instituicao_id")
	private Instituicao instituicao;
	
	public Denuncia() {}

	public Denuncia(String titulo, String descricao, String local, Instituicao instituicao) {
		this.titulo = titulo;
		this.descricao = descricao;
		this.local = local;
		setInstituicao(instituicao);
		this.dataCriacao = LocalDateTime.now();
	}
	
	@PrePersist
    private void preencherDataCriacao() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }
	
	public int getId() {
		return id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}
	
	public Instituicao getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(Instituicao instituicao) {
        if (instituicao == null) {
            throw new IllegalArgumentException("A denúncia precisa de uma instituição.");
        }
        if (this.instituicao == instituicao) {
            return;
        }
        if (this.instituicao != null) {
            this.instituicao.getDenuncias().remove(this);
        }
        this.instituicao = instituicao;
        if (!instituicao.getDenuncias().contains(this)) {
            instituicao.getDenuncias().add(this);
        }
    }
	
	@Override
    public String toString() {
        return "Denúncia #" + id + " | " + titulo + " | " + local
                + " | Instituição: " + instituicao.getNome();
    }
}
