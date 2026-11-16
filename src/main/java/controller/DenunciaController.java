package controller;

import javax.persistence.*;
import model.Denuncia;
import model.Instituicao;

/** CRUD e unidades de trabalho; não conhece Scanner nem imprime no terminal. */
public class DenunciaController {
    private final EntityManagerFactory emf;

    public DenunciaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Integer cadastrar(String titulo, String descricao, String local, int idInstituicao) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Instituicao instituicao = buscarInstituicao(em, idInstituicao);
            Denuncia denuncia = new Denuncia(titulo, descricao, local, instituicao);
            em.persist(denuncia);
            tx.commit();
            return denuncia.getId();
        } catch (RuntimeException e) {
            desfazer(tx);
            throw e;
        } finally {
            em.close();
        }
    }

    public void atualizar(int id, String titulo, String descricao, String local, int idInstituicao) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Busca novamente na unidade de trabalho: a entidade é gerenciada.
            Denuncia denuncia = buscarDenuncia(em, id);
            Instituicao instituicao = buscarInstituicao(em, idInstituicao);
            denuncia.setTitulo(titulo);
            denuncia.setDescricao(descricao);
            denuncia.setLocal(local);
            denuncia.setInstituicao(instituicao);
            // Não há update() nem merge(): o commit sincroniza os setters.
            tx.commit();
        } catch (RuntimeException e) {
            desfazer(tx);
            throw e;
        } finally {
            em.close();
        }
    }

    public void remover(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Denuncia denuncia = buscarDenuncia(em, id);
            em.remove(denuncia);
            tx.commit();
        } catch (RuntimeException e) {
            desfazer(tx);
            throw e;
        } finally {
            em.close();
        }
    }

    public Denuncia consultar(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            // ManyToOne é EAGER neste modelo: a instituição está disponível na view.
            return em.find(Denuncia.class, id);
        } finally {
            em.close();
        }
    }

    private Denuncia buscarDenuncia(EntityManager em, int id) {
        Denuncia denuncia = em.find(Denuncia.class, id);
        if (denuncia == null) {
            throw new IllegalArgumentException("Denúncia não encontrada.");
        }
        return denuncia;
    }

    private Instituicao buscarInstituicao(EntityManager em, int id) {
        Instituicao instituicao = em.find(Instituicao.class, id);
        if (instituicao == null) {
            throw new IllegalArgumentException("Instituição não encontrada. Operação cancelada.");
        }
        return instituicao;
    }

    private void desfazer(EntityTransaction tx) {
        if (tx.isActive()) {
            tx.rollback();
        }
    }

}
