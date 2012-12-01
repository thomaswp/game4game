import javax.swing.JApplet;
import javax.swing.JFrame;


public class Main {
	public static void main(String[] args) {
		//... Create an initialize the applet.
        JApplet theApplet = new AnimationTester();
        
        //... Create a window (JFrame) and make applet the content pane.
        JFrame window = new JFrame("Sample Applet and Application");
        window.setContentPane(theApplet);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();              // Arrange the components.
        //System.out.println(theApplet.getSize());
        window.setVisible(true);    // Make the window visible.
        

        theApplet.init();         // Needed if overridden in applet
        theApplet.start();        // Needed if overridden in applet

        window.setSize(800, 500);
	}
}
