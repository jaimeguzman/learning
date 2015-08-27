package LZ78;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class LZ78APP extends JFrame implements ActionListener {
	JFileChooser fc;
	JButton BrowesButton;
	JPanel panal;
	ButtonGroup chois;
	JLabel fileExtention;
	TextField outname;
	File file;
	JRadioButton dcom, com;
	JButton process;
	JTextField ext;
	Clip error, done;
	File f = new File("ERROR.wav"), d = new File("DONE.wav");
	final String Version = ".Lz78";
	static final String VrsionName = "Lz78";
	static final int VersionNumber = 1;

	LZ78APP() {
		super( VrsionName + " App V " + VersionNumber);
		setLayout(null);
		// setLayout(new FlowLayout());
		setBounds((1366 - 400) / 2, (768 - 240) / 2, 400, 240);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {

			error = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
			// error.open(AudioSystem.getAudioInputStream(f));

			done = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
			// done.open(AudioSystem.getAudioInputStream(d));

		} catch (Exception exc) {
			exc.printStackTrace(System.out);
		}

		fc = new JFileChooser();
		fc.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean accept(File f) {

				if (f.getAbsolutePath().contains(Version))
					return false;
				return true;
			}
		});

		panal = new JPanel();

		BrowesButton = new JButton();
		panal.add(BrowesButton);
		add(BrowesButton);

		BrowesButton.setBounds(250, 120, 90, 30);
		BrowesButton.setText("Browes");
		BrowesButton.setVisible(true);
		BrowesButton.addActionListener(this);

		JLabel label = new JLabel("Welcome "+ VrsionName + " App");
		panal.add(label);
		add(label);
		label.setBounds(135, 10, 120, 75);
		label.setVisible(true);

		com = new JRadioButton("Compres", true);
		panal.add(com);
		add(com);
		com.setSize(100, 20);
		com.setVisible(true);
		com.setBounds(80, 80, 130, 15);
		com.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				process.setEnabled(true);
				// fc = new JFileChooser();
				// fc.setFileFilter(null);
				fc.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public boolean accept(File f) {

						if (f.getAbsolutePath().contains(Version))
							return false;
						return true;
					}
				});
				outname.setText("");
				ext.setVisible(false);
			}
		});

		dcom = new JRadioButton("deCompres");
		panal.add(dcom);
		add(dcom);
		dcom.setSize(100, 20);
		dcom.setVisible(true);
		dcom.setBounds(210, 80, 130, 15);
		dcom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				process.setEnabled(true);
				// fc = new JFileChooser();
				outname.setText("");
				ext.setText("txt");
				ext.setVisible(true);
				fc.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public boolean accept(File f) {

						if (f.getName().indexOf(Version) != -1)
							return true;

						return false;
					}
				});
			}
		});

		chois = new ButtonGroup();
		chois.add(dcom);
		chois.add(com);

		fileExtention = new JLabel("Output File Name");
		panal.add(fileExtention);
		add(fileExtention);
		fileExtention.setVisible(true);
		fileExtention.setBounds(15, 100, 120, 20);

		outname = new TextField("");
		panal.add(outname);
		add(outname);
		outname.setVisible(true);
		outname.setBounds(15, 122, 120, 25);

		process = new JButton("Generate");
		panal.add(process);
		add(process);
		process.setVisible(true);
		process.setBounds(140, 160, 100, 30);
		process.addActionListener(this);

		ext = new JTextField();
		panal.add(ext);
		add(ext);
		ext.setVisible(false);
		ext.setBounds(145, 122, 35, 25);
		// outname .add("File Name");
		// add(panal);
	}

	@SuppressWarnings("resource")
	public void actionPerformed(ActionEvent e) {

		try {
			error.close();
			done.close();
			error.open(AudioSystem.getAudioInputStream(f));
			done.open(AudioSystem.getAudioInputStream(d));
		} catch (LineUnavailableException | IOException
				| UnsupportedAudioFileException e3) {
			e3.printStackTrace();
		}

		if (e.getSource() == BrowesButton) {
			int returnVal = fc.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {

				file = fc.getSelectedFile();
				// System.out.print(file.getAbsolutePath());
				if (com.isSelected()) {
					String temp = fc
							.getSelectedFile()
							.getName()
							.substring(
									fc.getSelectedFile().getName().indexOf('.'));
					outname.setText(fc
							.getSelectedFile()
							.getName()
							.substring(0,
									fc.getSelectedFile().getName().indexOf('.')));
					if (new String(Version).equals(temp)) {
						process.setEnabled(false);
						outname.setText("");
						error.start();
						JOptionPane.showMessageDialog(null,
								"Not Supported File.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				} else if (dcom.isSelected()) {
					String temp = fc
							.getSelectedFile()
							.getName()
							.substring(
									fc.getSelectedFile().getName().indexOf('.'));
					if (new String(Version).equals(temp)) {
						process.setEnabled(true);
						ext.setVisible(true);
						ext.setText("txt");
						outname.setText(fc
								.getSelectedFile()
								.getName()
								.substring(
										0,
										fc.getSelectedFile().getName()
												.indexOf('.')));
					} else {

						outname.setText(fc
								.getSelectedFile()
								.getName()
								.substring(
										0,
										fc.getSelectedFile().getName()
												.indexOf('.')));

						process.setEnabled(false);
						outname.setText("");
						error.start();
						JOptionPane.showMessageDialog(null,
								"Not Supported File.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}

		} else if (e.getSource() == process) {
			String path;
			try {
				path = file.getAbsolutePath().substring(0,
						file.getAbsolutePath().lastIndexOf('\\') + 1);
			} catch (Exception e2) {

				error.start();
				JOptionPane.showMessageDialog(null, "You Should Choose File ",
						"Error", JOptionPane.ERROR_MESSAGE);

				return;
			}

			tag.binaryNum = new ArrayList<Integer>();
			tag.binaryTag = new ArrayList<Boolean>();
			tag.numberTages = 0;

			if (com.isSelected()) {

				String data = "";
				Scanner reader;
				try {
					reader = new Scanner(new File(file.getAbsolutePath()));
					while (reader.hasNext()) {
						data += (reader.nextLine() + "\n");
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}

				binNum poi = new binNum();
				FileOutputStream outPut;
				try {
					outPut = new FileOutputStream(path + outname.getText()
							+ Version);

					DataOutputStream out = new DataOutputStream(outPut);

					ArrayList<tag> l = LZ78Main.compress(data, poi);


					poi.genrate();

					for (int i = 0; i < l.size(); i++) {
						// System.out.println(l.get(i).toString());
						l.get(i).prepareTag(poi);
					}

					tag.saveTag(out, poi );
					out.close();
					outPut.close();

					done.start();

					JOptionPane.showMessageDialog(null, outname.getText()
							+ Version + " Genarated", "Done",
							JOptionPane.PLAIN_MESSAGE);
					// fc = new JFileChooser();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			} else if (dcom.isSelected()) {

				binNum le = new binNum(), poi = new binNum();
				String deCompreseddata = "";
				InputStream i1n;
				try {
					i1n = new FileInputStream(new File(file.getAbsolutePath()));
					DataInputStream in = new DataInputStream(i1n);
					tag.readTag(in, poi, le);
					ArrayList<tag> kk = tag.deGenrate(poi );
					in.close();
					deCompreseddata = LZ78Main.deCompress(kk);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				try {
					BufferedWriter o = new BufferedWriter(new FileWriter(path
							+ outname.getText() + "." + ext.getText()));
					// System.out.print(deCompreseddata);
					for (int i = 0; i < deCompreseddata.length() - 1; i++) {
						if (deCompreseddata.charAt(i) == '\n')
							o.newLine();
						else
							o.write(deCompreseddata.charAt(i));
					}
					o.close();
					// File x = new File(path + outname.getText() + ".txt");
					// OutputStream o = new FileOutputStream(x);
					// o.write(deCompreseddata.getBytes());
					// o.close();

					done.start();
					JOptionPane.showMessageDialog(null, outname.getText() + "."
							+ ext.getText() + " Genarated", "Done",
							JOptionPane.PLAIN_MESSAGE);

				} catch (Exception e1) {

					e1.printStackTrace();
				}
			}
			fc.setSelectedFile(null);
		}
	}

}
