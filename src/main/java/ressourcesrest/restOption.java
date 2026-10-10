package ressourcesrest;

import entities.Option;
import metiers.OptionBusiness;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("options")
public class restOption {
   public static OptionBusiness optB= new OptionBusiness();

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getOptions(@QueryParam("domaine") String d){
      List <Option>l= new ArrayList<>();
      if(d==null){ l= optB.getListeOptions();}
      else {l= optB.getOptionsByDomaine(d);}

      if(l.isEmpty()){
           return Response.status(Response.Status.NO_CONTENT).build();

          }
           return Response.status(200).entity(l).build();
   }
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public Response addNewOption(Option op) {
      if (!optB.addOption(op)) {
         return Response.status(Response.Status.BAD_REQUEST).build();

      }
      return Response.status(200).build();

   }

//@GET
//@Produces(MediaType.APPLICATION_JSON)
 //  public Response getOptionByDomaine(@QueryParam("domaine") string d){
  //    List <Option>l= optB.getOptionsByDomaine(d);
   //   if(l.isEmpty()){
      //   return Response.status(Response.Status.NO_CONTENT).build();

     // }
    //  return Response.status(200).entity(l).build();
  // }
//
   @DELETE
   @Path("{code}")
public Response deleteOption(@PathParam("code") int id){
     if(optB.deleteOption(id)){
      return Response.status(Response.Status.NO_CONTENT).build();
     }
      return Response.status(Response.Status.NOT_FOUND).build();
}
}
