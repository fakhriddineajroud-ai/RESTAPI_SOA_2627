package ressources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import entities.Credentials;
import utilities.JwtService;

@Path("authentication")
public class AuthenticationEndPoint {

    @Context
    private UriInfo uriInfo;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser(Credentials cred) {
        try {
            // 1. Vérifier les identifiants
            authenticate(cred.getUsername(), cred.getPassword());

            // 2. Générer le jeton pour cet utilisateur
            String issuer = uriInfo.getBaseUri().toString();
            String token = JwtService.generateAccessToken(cred.getUsername(), issuer);

            // 3. Renvoyer le jeton au client
            return Response.ok(token).build();

        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    private void authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            throw new SecurityException("Identifiants invalides");
        }
    }
}
