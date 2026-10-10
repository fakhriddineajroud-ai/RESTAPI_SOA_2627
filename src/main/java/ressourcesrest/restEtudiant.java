package resourcesrest;

import entities.Etudiant;
import entities.Option;
import metiers.EtudiantBusiness;
import metiers.OptionBusiness;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Path("/etudiants")
public class restEtudiant {

    // Les objets métier sont créés une seule fois
    private static final EtudiantBusiness etudiantBusiness =
            new EtudiantBusiness();

    private static final OptionBusiness optionBusiness =
            new OptionBusiness();

    // 1. Ajouter un étudiant
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response ajouterEtudiant(Etudiant etudiant) {

        if (etudiant == null || etudiant.getOption() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        boolean resultat = etudiantBusiness.addEtudiant(etudiant);

        if (resultat) {
            return Response.ok(etudiant).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response afficherEtudiants() {

        List<Etudiant> liste =
                etudiantBusiness.getAllEtudiants();

        return Response.ok(liste).build();
    }

    @GET
    @Path("/{identifiant}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rechercherEtudiant(
            @PathParam("identifiant") String identifiant) {

        Etudiant etudiant =
                etudiantBusiness.getEtudiantByIdentifiant(identifiant);

        if (etudiant != null) {
            return Response.ok(etudiant).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{identifiant}")
    public Response supprimerEtudiant(
            @PathParam("identifiant") String identifiant) {

        boolean resultat =
                etudiantBusiness.deleteEtudiant(identifiant);

        if (resultat) {
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @PUT
    @Path("/{identifiant}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifierEtudiant(
            @PathParam("identifiant") String identifiant,
            Etudiant etudiant) {

        if (etudiant == null || etudiant.getOption() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // Vérifier que l'option existe
        Option option = optionBusiness.getOptionByCode(
                etudiant.getOption().getCodeOption());

        if (option == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Associer l'objet Option existant à l'étudiant
        etudiant.setOption(option);

        boolean resultat =
                etudiantBusiness.updateEtudiant(identifiant, etudiant);

        if (resultat) {
            return Response.ok(etudiant).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }



    @GET
    @Path("/option")
    @Produces(MediaType.APPLICATION_XML)
    public Response afficherEtudiantsParOption(
            @QueryParam("codeOption") int codeOption) {

        Option option = optionBusiness.getOptionByCode(codeOption);

        if (option == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Etudiant> liste =
                etudiantBusiness.getEtudiantsByOption(option);

        return Response.ok(new EtudiantListXML(liste)).build();
    }

    // Enveloppe XML pour la liste des étudiants
    @XmlRootElement(name = "etudiants")
    public static class EtudiantListXML {

        private List<Etudiant> etudiants;

        public EtudiantListXML() {
        }

        public EtudiantListXML(List<Etudiant> etudiants) {
            this.etudiants = etudiants;
        }

        @XmlElement(name = "etudiant")
        public List<Etudiant> getEtudiants() {
            return etudiants;
        }

        public void setEtudiants(List<Etudiant> etudiants) {
            this.etudiants = etudiants;
        }
    }
}