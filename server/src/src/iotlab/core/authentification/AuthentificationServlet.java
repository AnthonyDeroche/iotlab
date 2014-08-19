/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.authentification;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@WebServlet("/login")
public class AuthentificationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
	@EJB
	private AccountManager authentification;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthentificationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(!authentification.isLoggedIn(request.getSession()))
			request.getRequestDispatcher("index.jsp?page=login").forward(request, response);
		else{
			request.setAttribute("user", request.getSession().getAttribute(AccountManager.USER_SESSION));
			request.getRequestDispatcher("index.jsp?page=logout").forward(request, response);
		}
	}
}
