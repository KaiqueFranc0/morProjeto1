package controller;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import model.Instituicao;

/** Cadastro e consulta de instituições, sem interação direta com o terminal. */
public class InstituicaoController {
    private final EntityManagerFactory emf;

    public InstituicaoController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Integer cadastrar(String nome, String email) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // A restrição UNIQUE do banco impede e-mails duplicados, sem consulta prévia.
            Instituicao instituicao = new Instituicao(nome, email);
            em.persist(instituicao);
            tx.commit();
            return instituicao.getId();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Instituicao consultar(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            Instituicao instituicao = em.find(Instituicao.class, id);
            if (instituicao != null) {
                // Inicializa a coleção LAZY enquanto o EntityManager está aberto.
                // Depois disso a view pode percorrer as denúncias sem conexão aberta.
                instituicao.getDenuncias().size();
            }
            return instituicao;
        } finally {
            em.close();
        }
    }
}
