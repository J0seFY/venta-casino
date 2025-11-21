import controlador.ControladorCasino;
import vista.VistaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ControladorCasino controlador = new ControladorCasino();
            VistaPrincipal vista = new VistaPrincipal(controlador);
            vista.setVisible(true);
        });
    }
}