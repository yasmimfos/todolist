package br.com.yasmimsilva.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.yasmimsilva.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAut extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();
        if (servletPath.startsWith("/tasks/")) {

            var authname = request.getHeader("Authorization");
            var authEncoded = authname.substring("Basic".length()).trim();
            byte[] authdecode = Base64.getDecoder().decode(authEncoded);
            var authString = new String(authdecode);
            String[] credencials = authString.split(":");
            String username = credencials[0];
            String password = credencials[1];

            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(404, "Usuário não encnontrado");
            } else {
                var passVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passVerify.verified) {
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }

    }

}