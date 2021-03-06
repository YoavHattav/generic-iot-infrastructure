package il.co.ilrd.iot_servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	CompaniesCrud compCrud;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        compCrud = new CompaniesCrud("jdbc:mysql://localhost:3306/GenericIOT", "root", "ct,h kvmkhj");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String jsonString;
		JsonObject json;
		System.out.println("im posting in login");
		try {
			// FIXME close the reader
			BufferedReader reader = request.getReader();
			jsonString = reader.lines().collect(Collectors.joining());
			json =  JsonParser.parseString(jsonString).getAsJsonObject();
		} catch (Exception e) {
			// crash and burn
			throw new IOException("Error parsing JSON request string");
		}
		 
		CompanyDetails details = compCrud.read(json.get("email").getAsString());
		
		if (details == null) {
			out.println(Status.EMAIL_NOT_FOUND.getDescription());
			response.setStatus(Status.EMAIL_NOT_FOUND.getCode());
		}
		else if (!details.getPassword().equals(json.get("password").getAsString())) {
			response.setStatus(Status.WRONG_PASSWORD.getCode());
			out.println(Status.WRONG_PASSWORD.getDescription());
		}
		else {
			//return token
			response.setStatus(Status.OK.getCode());
			out.println(TokenManager.generateToken(json.get("email").getAsString()));
		}
		out.close();
	}
	
}
