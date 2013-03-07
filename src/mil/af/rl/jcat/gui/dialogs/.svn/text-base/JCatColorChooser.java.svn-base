package mil.af.rl.jcat.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.BufferedImage;

/**
 * ColorChooser to include alpha selection -modified version of JWBColorChooser
 * from JWB
 */
public class JCatColorChooser extends JDialog implements ActionListener, ChangeListener
{

	private static final long serialVersionUID = 1L;
	private PreviewCanvas previewCanvas = new PreviewCanvas();
	private BufferedImage backgroundImage = null;
	private JSlider[] colorSliders;
	private JSpinner[] colorSpinners;
	private Color newColor = null;

	public JCatColorChooser(Component parent, Color currColor)
	{
		//newColor = currColor;
		// create color labels, sliders, markers and spinners
		JLabel[] colorLabels = new JLabel[] { new JLabel("Red"),
				new JLabel("Green"), new JLabel("Blue"), new JLabel("Alpha") };

		colorSliders = new JSlider[] { new JSlider(0, 255, currColor.getRed()),
				new JSlider(0, 255, currColor.getGreen()),
				new JSlider(0, 255, currColor.getBlue()),
				new JSlider(0, 255, currColor.getAlpha()) };

		colorSpinners = new JSpinner[] {
				new JSpinner(new SpinnerNumberModel(colorSliders[0].getValue(),
						0, 255, 1)),
				new JSpinner(new SpinnerNumberModel(colorSliders[1].getValue(),
						0, 255, 1)),
				new JSpinner(new SpinnerNumberModel(colorSliders[2].getValue(),
						0, 255, 1)),
				new JSpinner(new SpinnerNumberModel(colorSliders[3].getValue(),
						0, 255, 1)) };

		// set up GUI layout
		Container contentPane = getContentPane();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		contentPane.setLayout(gridbag);
		constraints.fill = GridBagConstraints.BOTH;

		// setup canvas
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 3;
		constraints.insets = new Insets(3, 3, 3, 3);
		previewCanvas.setPreferredSize(new Dimension(150, 50));
		gridbag.setConstraints(previewCanvas, constraints);
		contentPane.add(previewCanvas);

		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		JButton applyButton = new JButton("OK");
		applyButton.addActionListener(this);
		applyButton.setPreferredSize(new Dimension(72, 0));
		gridbag.setConstraints(applyButton, constraints);
		contentPane.add(applyButton);

		constraints.gridx = 1;
		constraints.gridy = 3;
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		gridbag.setConstraints(cancelButton, constraints);
		contentPane.add(cancelButton);

		// add color labels
		for (int i = 0; i < colorLabels.length; i++)
		{
			constraints.gridx = 2;
			constraints.gridy = i;
			gridbag.setConstraints(colorLabels[i], constraints);
			contentPane.add(colorLabels[i]);
		}

		// add color sliders
		for (int i = 0; i < colorSliders.length; i++)
		{
			constraints.gridx = 3;
			constraints.gridy = i;
			colorSliders[i].setName("" + i);
			colorSliders[i].addChangeListener(this);
			gridbag.setConstraints(colorSliders[i], constraints);
			contentPane.add(colorSliders[i]);
		}

		// add color spinners
		for (int i = 0; i < colorSpinners.length; i++)
		{
			constraints.gridx = 4;
			constraints.gridy = i;
			colorSpinners[i].setName("" + i);
			colorSpinners[i].addChangeListener(this);
			gridbag.setConstraints(colorSpinners[i], constraints);
			contentPane.add(colorSpinners[i]);
		}

		// finalize GUI setup
		this.setModal(true);
//		this.setLocation(parent.getLocationOnScreen().x, parent.getLocationOnScreen().y + 50);
		this.setLocationRelativeTo(parent);
		this.setTitle("Choose new color");
		this.setResizable(false);
		this.pack();

		// setup and initialize the background image and sameple canvas
		backgroundImage = new BufferedImage(previewCanvas.getWidth(), previewCanvas.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) backgroundImage.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, previewCanvas.getWidth(), previewCanvas.getHeight());
		graphics.setColor(Color.BLACK);
		graphics.setFont(new Font("Arial", 1, 14));
		graphics.drawString("Transparency", 27,	previewCanvas.getHeight() / 2 + 5);
		graphics.setStroke(new BasicStroke(2));
		graphics.drawRect(0, 0, previewCanvas.getWidth() - 1, previewCanvas.getHeight() - 1);
		graphics.dispose();

		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("OK"))
			newColor = (new Color(colorSliders[0].getValue(), colorSliders[1].getValue(), colorSliders[2].getValue(), colorSliders[3].getValue()));
		dispose();
	}

	public Color getNewColor()
	{
		return newColor;
	}

	
	public void stateChanged(ChangeEvent e)
	{
		// if spinner changed
		if (e.getSource() instanceof JSpinner)
		{
			int spinnerId = Integer.valueOf(((JSpinner) e.getSource()).getName()).intValue();
			colorSliders[spinnerId].setValue(((Integer) colorSpinners[spinnerId].getValue()).intValue());
		}
		else // else slider changed
		{
			int sliderId = Integer.valueOf(((JSlider) e.getSource()).getName()).intValue();
			colorSpinners[sliderId].setValue(new Integer(colorSliders[sliderId].getValue()));
		}
		previewCanvas.repaint();
	}

	private class PreviewCanvas extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public void paintComponent(Graphics g)
		{
			Graphics2D graphics = (Graphics2D) g;
			graphics.drawImage(backgroundImage, null, 0, 0);
			graphics.setColor(new Color(colorSliders[0].getValue(),	colorSliders[1].getValue(), colorSliders[2].getValue(),	colorSliders[3].getValue()));
			graphics.fillRect(0, 0, previewCanvas.getWidth(), previewCanvas.getHeight());
			graphics.dispose();
		}
	}
}
