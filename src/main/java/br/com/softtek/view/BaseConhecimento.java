package br.com.softtek.view;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseConhecimento extends Frame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseConhecimento.class);
	private JFrame frame;
	private JLabel lblBreadCrumb;
	private static final String DIRETORIO_BASE_RAIZ_DEFAULT = "./BASE-CONHECIMENTO/.";
	private static String DIRETORIO_BASE_RAIZ;
	private List<String> stackDir = new ArrayList<String>();
	private JScrollPane scrollPaneArquivos;
	private JTable tabelaArquivos;
	private JButton buttonVoltar;
	private JPanel panelAssuntos;
	private JLabel lblDirBaseConhecimento;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BaseConhecimento arvoreConhecimento = new BaseConhecimento();
					arvoreConhecimento.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	} 

	public BaseConhecimento() throws Exception {
		initialize();
	}

	private void initialize() throws Exception {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setType(Window.Type.POPUP);
		frame.setTitle("Base de Conhecimento Softtek - Petrobras");
		frame.setBounds(100, 100, 855, 595);
		frame.setDefaultCloseOperation(3);
		frame.getContentPane().setLayout(null);
		
		panelAssuntos = new JPanel();
		panelAssuntos.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelAssuntos.setBounds(10, 54, 829, 284);
		frame.getContentPane().add(panelAssuntos);
		panelAssuntos.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblAssunto = new JLabel("Selecione um tópico:");
		lblAssunto.setHorizontalAlignment(SwingConstants.CENTER);
		lblAssunto.setFont(new Font("Arial", Font.PLAIN, 14));
		lblAssunto.setBounds(109, 11, 631, 32);
		frame.getContentPane().add(lblAssunto);
		
		lblBreadCrumb = new JLabel();
		lblBreadCrumb.setBounds(10, 35, 770, 14);
		frame.getContentPane().add(lblBreadCrumb);
		
		scrollPaneArquivos = new JScrollPane();
		scrollPaneArquivos.setSize(829, 182);
		scrollPaneArquivos.setLocation(10, 349);
		frame.getContentPane().add(scrollPaneArquivos);
		
		buttonVoltar = new JButton("<<");
		buttonVoltar.setToolTipText("Voltar nível");
		buttonVoltar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					stackDir.remove(stackDir.size() - 1);
					String diretorio = getDir();
					montarTabelas(diretorio, panelAssuntos);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		buttonVoltar.setBounds(790, 26, 49, 23);
		frame.getContentPane().add(buttonVoltar);
		
		DIRETORIO_BASE_RAIZ = getDiretorioBaseRaizConhecimento();
		montarTabelas(null, panelAssuntos);
		
		lblDirBaseConhecimento = new JLabel("Diretório base de conhecimento: " + DIRETORIO_BASE_RAIZ);
		lblDirBaseConhecimento.setToolTipText("Pode-se definir o diretório criando a variável de ambiente DIRETORIO_BASE_CONHECIMENTO com o valor desejado");
		lblDirBaseConhecimento.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblDirBaseConhecimento.setBounds(10, 536, 829, 14);
		frame.getContentPane().add(lblDirBaseConhecimento);
	}

	private void montarTabelas(String diretorioBaseRaiz, final JPanel panel) throws Exception {
		clean(panel);
		List<String> diretorios = getItens(diretorioBaseRaiz, true);
		List<String> arquivos = getItens(diretorioBaseRaiz, false);
		montarDiretorios(panel, diretorios);
		montarArquivos(panel, arquivos);
		atualizarBreadCrumb();
		controlarBotaoVoltar();
	}

	private void controlarBotaoVoltar() {
		if(stackDir.isEmpty()){
			buttonVoltar.setVisible(false);
		}else{
			buttonVoltar.setVisible(true);
		}
	}

	private void montarArquivos(JPanel panel, List<String> arquivos) throws Exception {
		tabelaArquivos = new JTable();
		tabelaArquivos.setFillsViewportHeight(true);
		
		Object[][] model = getModel(arquivos);
		String[] colunas = new String[]{"Procedimentos/Arquivos"};
		DefaultTableModel tableModel = new DefaultTableModel(model, colunas);
		tabelaArquivos.setModel(tableModel);
		
		scrollPaneArquivos.setViewportView(tabelaArquivos);
		
		tabelaArquivos.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				JTable target = (JTable) e.getSource();
				int row = target.getSelectedRow();
				int column = target.getSelectedColumn();
				try {
					Desktop.getDesktop().open(new File(DIRETORIO_BASE_RAIZ + "\\" + getDir() + "\\" + target.getModel().getValueAt(row, column).toString()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	private Object[][] getModel(List<String> arquivos) throws Exception {
		Object[][] model = new Object[arquivos.size()][1];
		int i = 0;
		for (String arquivo : arquivos) {
			model[i][0] = arquivo;
			i++;
		}
		return model;
	}

	private void montarDiretorios(final JPanel panel, List<String> diretorios) {
		JButton btnAssunto;
		for(String diretorio : diretorios){
			btnAssunto = new JButton(diretorio);
			panel.add(btnAssunto);
			btnAssunto.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent arg0) {
					JButton buttonClicked = ((JButton)arg0.getSource());
					stackDir.add(buttonClicked.getText());
					String diretorio = getDir();
					try {
						montarTabelas(diretorio, panel);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	private void atualizarBreadCrumb() {
		lblBreadCrumb.setText(getDir());
	}
	
	private String getDir() {
		StringBuilder diretorioBuilder = new StringBuilder();
		for(String diretorio : stackDir){
			diretorioBuilder.append("/");
			diretorioBuilder.append(diretorio);
		}
		return diretorioBuilder.toString();
	}
	
	private void clean(final JPanel panel) {
		panel.removeAll();
		panel.updateUI();
	}	
	
	private String getDiretorioBaseRaizConhecimento() throws Exception{
		String diretorio = "";
		if(new File(DIRETORIO_BASE_RAIZ_DEFAULT).exists()){
			diretorio = DIRETORIO_BASE_RAIZ_DEFAULT;
		}else if(System.getenv("DIRETORIO_BASE_CONHECIMENTO") != null){
			diretorio = System.getenv("DIRETORIO_BASE_CONHECIMENTO");
		}
		if(diretorio != null && !"".equals(diretorio)){
			LOGGER.info("Usando diretorio " + diretorio);
			return diretorio;
		}
		throw new Exception("Diretorio raiz nao encontrado. Pode-se definir a variavel de ambiente DIRETORIO_BASE_CONHECIMENTO com o valor desejado");
	}
	
	private List<String> getItens(String diretorioBase, boolean isDir) {
		List<String> itens = new ArrayList<String>();
		File diretorio = new File(DIRETORIO_BASE_RAIZ + (diretorioBase != null ? diretorioBase : ""));
		if(diretorio.exists() && diretorio.listFiles() != null){
			for(File file : diretorio.listFiles()){
				if(isDir && file.isDirectory()){
					itens.add(file.getName());
				}
				if(!isDir && file.isFile()){
					itens.add(file.getName());
				}
			}
		}
		return itens;
	}
}
