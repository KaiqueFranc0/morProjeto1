import controller.DenunciaController;
import controller.InstituicaoController;
import java.util.Scanner;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import service.EmailService;
import view.DenunciaView;

/** Ponto de entrada: inicializa os componentes MVC e encerra os recursos. */
public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        try (Scanner scanner = new Scanner(System.in)) {
            // Conexão configurada exclusivamente no persistence.xml, sem mapas.
            emf = Persistence.createEntityManagerFactory("morProjeto1");
            DenunciaController denunciaController = new DenunciaController(emf);
            InstituicaoController instituicaoController = new InstituicaoController(emf);
            EmailService emailService = new EmailService();
            DenunciaView view = new DenunciaView(
                    scanner, denunciaController, instituicaoController, emailService);
            view.executar();
        } catch (RuntimeException e) {
            DenunciaView.exibirFalhaInicializacao(e);
        } finally {
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }
}
