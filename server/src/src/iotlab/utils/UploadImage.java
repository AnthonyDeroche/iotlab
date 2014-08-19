/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.utils;

import iotlab.core.authentification.AccountManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * 
 * @author Arthur Garnier
 *
 */
@MultipartConfig
@WebServlet("/upload")
public class UploadImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CHEMIN = "~/";

	@EJB
	private AccountManager accountManager;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadImage() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		if(accountManager.isLoggedIn(request.getSession())){
			Part part = request.getPart("fichier");
			//String nomFichier = part.getSubmittedFileName();
			BufferedInputStream entree = new BufferedInputStream(
					part.getInputStream(), 1024 * 1024 * 5);
			File f = new File(CHEMIN + "map");
			if(!f.exists()){
				new File(CHEMIN).mkdirs();
				f.createNewFile();
			}
			else{
				f.delete();
			}
			System.out.println(f.getAbsolutePath());
			BufferedOutputStream sortie = new BufferedOutputStream(
					new FileOutputStream(f),
					1024 * 1024 * 5);
			byte[] tampon = new byte[1024 * 1024 * 5];
			int longueur;
			while ((longueur = entree.read(tampon)) > 0) {
				sortie.write(tampon, 0, longueur);
			}
			sortie.close();
			entree.close();
		}
		response.sendRedirect("map?config=live");
	}
}
