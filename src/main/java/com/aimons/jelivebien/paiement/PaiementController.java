package com.aimons.jelivebien.paiement;



import com.aimons.jelivebien.model.CountPaiementStatus;
import com.aimons.jelivebien.model.User;
import com.aimons.jelivebien.model.UserPaiement;
import com.aimons.jelivebien.repository.CountPaiementStatusRepository;
import com.aimons.jelivebien.repository.UserPaiementRepository;
import com.aimons.jelivebien.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;




import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;


@Controller
public class PaiementController  {

    // DEMO PAY
 /*   public static final String CAMPAY_NET_API_GET_PAYMENT_LINK = "https://demo.campay.net/api/get_payment_link/";

    private static final String BODYTOKEN = "{\n" +
            "\"username\": \"Dd_HRETjl2dC9O--xB2qnVxT_Tm-EIVvUc0QST5hG38tpnh3gWkiCPiT3EkLgHxOYm2SrdtW50d8w\",\n" +
            " \"password\":\"9V6Xs0e-l28hZEDlvgXQvwVflNmuZsf5kCq1bjiT_W_bJuBcPy1UprGoBWlY4L9bPZjYkBecFsQ\" \n}";

            public static final String HTTPS_CAMPAY_NET_API_TOKEN = "https://demo.campay.net/api/token/";
*/

    //LIVE PAY
    public static final String CAMPAY_NET_API_GET_PAYMENT_LINK = "https://www.campay.net/api/get_payment_link/";

    private static final String BODYTOKEN = "{\n" +
            "\"username\": \"4oqZcnry6qtnfgwROSTEc6otOJU1jHC7F8S8Lv-G7fmyPRGQE8RPed7mgLZP078eMSGmDdUxxyztQ\",\n" +
            " \"password\":\"TSezNv4AVlMhaul5Pc0_hi1dwCCgfn4hdAKWdZNMdwGLOi5T6CRrKmzxsG9_L2Qz0UmUKhzx_6sQg\" \n}";

    public static final String HTTPS_CAMPAY_NET_API_TOKEN = "https://www.campay.net/api/token/";


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPaiementRepository userPaiementRepository;

    @Autowired
    private CountPaiementStatusRepository countPaiementStatusRepository;


    HttpClient client = HttpClient.newHttpClient();




    private HttpRequest getHttpRequestTokenFromCampay() {
        return HttpRequest.newBuilder()
                .uri(URI.create(HTTPS_CAMPAY_NET_API_TOKEN))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(BODYTOKEN))
                .build();
    }

    /**
     * paiement VIP
     * @param redirectAttributes
     * @return
     * @throws IOException
     */
    @PostMapping("/paiement_vip")
    public ModelAndView  paiement(RedirectAttributes redirectAttributes, HttpServletRequest servletRequest) throws IOException, JSONException, InterruptedException {




        HttpRequest requestToken = getHttpRequestTokenFromCampay();

        HttpResponse<String> responseToken = client.send(requestToken, HttpResponse.BodyHandlers.ofString());
        System.out.println(responseToken.statusCode());
        System.out.println(responseToken.body());


        JSONObject jsonObjectToken = new JSONObject(responseToken.body());
        String token = jsonObjectToken.getString("token");



        //we take the user who is authenticate in the session
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
        User user = userRepository.findByEmail(username);
        String firstName = user.getFirstName();
        String lastName = user.getLastName();


        redirectAttributes.addAttribute("user",user);

        String external_reference = "PAY_VIP_"+username;

        String redirect_URL_VIP = getSiteURL(servletRequest)+"/verify_paiement";




        String contentVip = "  {\n" +
                "            \"amount\":10000,\n" +
                "                \"currency\":\"XAF\",\n" +
                "                \"description\":\"Test\",\n" +
                "                \"first_name\":\"John\",\n" +
                "                \"last_name\":\"Doe\",\n" +
                "                \"email\":\"example@mail.com\",\n" +
                "                \"external_reference\":\"ref\",\n" +
                "                \"redirect_url\":\"redirect_URL_VIP\",\n" +
                "                \"payment_options\":\"MOMO,CARD\"\n" +
                "        }";

        String content1 = contentVip.replace("John",firstName);
        String content2 =content1.replace("Doe",lastName);
        String content3 = content2.replace("example@mail.com",username);
        String content4 =content3.replace("Test","PAY_VIP_"+username);
        String content5 =content4.replace("ref","PAY_VIP_"+username);
        String content6 = content5.replace("redirect_URL_VIP",redirect_URL_VIP);

        String contentUserVip = content6;




        HttpRequest requestVIP = HttpRequest.newBuilder()
                .uri(URI.create(CAMPAY_NET_API_GET_PAYMENT_LINK))
                .header("Content-Type", "application/json")
                .header("Authorization","Token "+token)
                .POST(HttpRequest.BodyPublishers.ofString(contentUserVip))
                .build();

        HttpResponse<String> responseVIP = client.send(requestVIP, HttpResponse.BodyHandlers.ofString());
        System.out.println(responseVIP.statusCode());
        System.out.println(responseVIP.body());


        String url_pay_vip = responseVIP.body();

        JSONObject jsonObjectUrl = new JSONObject(url_pay_vip);

        String url_paiement = jsonObjectUrl.getString("link");

        return new ModelAndView("redirect:"+url_paiement);





    }



    /**
     *
     * @param redirectAttributes
     * @param servletRequest
     * @return
     * @throws IOException
     */
    @PostMapping("/paiement_premium")
    public ModelAndView  paiementPremium(RedirectAttributes redirectAttributes, HttpServletRequest servletRequest) throws IOException, InterruptedException, JSONException {


        HttpRequest requestToken = getHttpRequestTokenFromCampay();

        HttpResponse<String> responseToken = client.send(requestToken, HttpResponse.BodyHandlers.ofString());
        System.out.println(responseToken.statusCode());
        System.out.println(responseToken.body());


        JSONObject jsonObjectToken = new JSONObject(responseToken.body());
        String token = jsonObjectToken.getString("token");

        //we take the user who is authenticate in the session
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
        User user = userRepository.findByEmail(username);
        String firstName = user.getFirstName();
        String lastName = user.getLastName();


        redirectAttributes.addAttribute("user",user);

        String external_reference = "PAY_PRE_"+username;


        String redirect_URL_PREMIUM = getSiteURL(servletRequest)+"/verify_paiement_premium";



        String contentPRE = "  {\n" +
                "            \"amount\":7500,\n" +
                "                \"currency\":\"XAF\",\n" +
                "                \"description\":\"Test\",\n" +
                "                \"first_name\":\"John\",\n" +
                "                \"last_name\":\"Doe\",\n" +
                "                \"email\":\"example@mail.com\",\n" +
                "                \"external_reference\":\"ref\",\n" +
                "                \"redirect_url\":\"redirect_URL_PREMIUM\",\n" +
                "                \"payment_options\":\"MOMO,CARD\"\n" +
                "        }";

        String content1 = contentPRE.replace("John",firstName);
        String content2 =content1.replace("Doe",lastName);
        String content3 = content2.replace("example@mail.com",username);
        String content4 =content3.replace("Test","PAY_PRE_"+username);
        String content5 =content4.replace("ref","PAY_PRE_"+username);
        String content6 = content5.replace("redirect_URL_PREMIUM",redirect_URL_PREMIUM);

        String contentUserPre = content6;



        HttpRequest requestPREMIUM = HttpRequest.newBuilder()
                .uri(URI.create(CAMPAY_NET_API_GET_PAYMENT_LINK))
                .header("Content-Type", "application/json")
                .header("Authorization","Token "+token)
                .POST(HttpRequest.BodyPublishers.ofString(contentUserPre))
                .build();

        HttpResponse<String> responsePREMIUM = client.send(requestPREMIUM, HttpResponse.BodyHandlers.ofString());
        System.out.println(responsePREMIUM.statusCode());
        System.out.println(responsePREMIUM.body());


        String url_pay_premium = responsePREMIUM.body();

        JSONObject jsonObjectUrl = new JSONObject(url_pay_premium);

        String url_paiement = jsonObjectUrl.getString("link");

        return new ModelAndView("redirect:"+url_paiement);


    }





    @PostMapping("/paiement_standard")
    public ModelAndView  paiementStandard(RedirectAttributes redirectAttributes,HttpServletRequest servletRequest) throws IOException, InterruptedException, JSONException {

        HttpRequest requestToken = getHttpRequestTokenFromCampay();

        HttpResponse<String> responseToken = client.send(requestToken, HttpResponse.BodyHandlers.ofString());
        System.out.println(responseToken.statusCode());
        System.out.println(responseToken.body());


        JSONObject jsonObjectToken = new JSONObject(responseToken.body());
        String token = jsonObjectToken.getString("token");


        //we take the user who is authenticate in the session
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
        User user = userRepository.findByEmail(username);
        String firstName = user.getFirstName();
        String lastName = user.getLastName();


        redirectAttributes.addAttribute("user",user);

        String external_reference = "PAY_STD_"+username;

        String redirect_URL_STD = getSiteURL(servletRequest)+"/verify_paiement_standard";



        String contentSTD = "  {\n" +
                "            \"amount\":5000,\n" +
                "                \"currency\":\"XAF\",\n" +
                "                \"description\":\"Test\",\n" +
                "                \"first_name\":\"John\",\n" +
                "                \"last_name\":\"Doe\",\n" +
                "                \"email\":\"example@mail.com\",\n" +
                "                \"external_reference\":\"ref\",\n" +
                "                \"redirect_url\":\"redirect_URL_STD\",\n" +
                "                \"payment_options\":\"MOMO,CARD\"\n" +
                "        }";

        String content1 = contentSTD.replace("John",firstName);
        String content2 =content1.replace("Doe",lastName);
        String content3 = content2.replace("example@mail.com",username);
        String content4 =content3.replace("Test","PAY_STD_"+username);
        String content5 =content4.replace("Test","PAY_STD_"+username);
        String content6 = content5.replace("redirect_URL_STD",redirect_URL_STD);

        String contentUserSTD = content6;

        HttpRequest requestSTD = HttpRequest.newBuilder()
                .uri(URI.create(CAMPAY_NET_API_GET_PAYMENT_LINK))
                .header("Content-Type", "application/json")
                .header("Authorization","Token "+token)
                .POST(HttpRequest.BodyPublishers.ofString(contentUserSTD))
                .build();

        HttpResponse<String> responseSTD = client.send(requestSTD, HttpResponse.BodyHandlers.ofString());
        System.out.println(responseSTD.statusCode());
        System.out.println(responseSTD.body());


        String url_pay_standard = responseSTD.body();

        JSONObject jsonObjectUrl = new JSONObject(url_pay_standard);

        String url_paiement = jsonObjectUrl.getString("link");

        return new ModelAndView("redirect:"+url_paiement);


    }



    /**
     * verify paiement vip
     * @param external_reference
     * @param status
     * @param amount
     * @param currency
     * @param operator
     * @param code
     * @param operator_reference
     * @param redirectAttributes
     * @return
     */
    @GetMapping("/verify_paiement")
    public ModelAndView verify_user_paiement(@RequestParam("external_reference") String external_reference,
                                       @RequestParam("status") String status,
                                       @RequestParam("amount") String amount,
                                       @RequestParam("currency") String currency,
                                       @RequestParam("operator") String operator,
                                       @RequestParam("code") String code,
                                       @RequestParam("operator_reference") String operator_reference,
                                       @RequestParam("extra_email") String email,
                                       RedirectAttributes redirectAttributes
                                       ){



        if(status.equals("SUCCESSFUL")){

            UserPaiement userPaiement = new UserPaiement();

            //String userN = external_reference.substring(8);

           User user = userRepository.findByEmail(email);

            if(user !=null){

                userPaiement.setUser_paiement(user);
                userPaiement.setAmount(amount);
                userPaiement.setCode(code);
                userPaiement.setCurrency(currency);
                userPaiement.setOperator(operator);
                userPaiement.setExternalReference(external_reference);
                userPaiement.setOperatorReference(operator_reference);
                userPaiement.setStatus(status);
                userPaiement.setPaySuccess(true);
                userPaiement.setDatePaiement(LocalDateTime.now());

                userPaiementRepository.save(userPaiement);

                //TODO: set the date of end of subscribe at 1 month. the number MAX of posts a 30
                User user1 = userRepository.findByEmail(user.getEmail());
                if(user1 != null){
                    //we set subscribe at true,
                    user1.setSubscribe(true);
                    user1.setNumberPostPermits(30);
                    user1.setTypeAccount(User.TypeAccount.VIP);
                    user1.setExpireSubscribeDate(LocalDateTime.now().plusDays(30));

                    userRepository.save(user1);
                }

                CountPaiementStatus countPaiementStatus = new CountPaiementStatus();
                countPaiementStatus.setFailed(status);
                countPaiementStatusRepository.save(countPaiementStatus);

                String success = "Felicitations \n" +
                        "Votre paiement est pris en compte.";

                redirectAttributes.addAttribute("messagePaiement",success);

                return new ModelAndView("redirect:/userpanel");

            }


        }

        if(status.equals("FAILED")){

            CountPaiementStatus countPaiementStatus = new CountPaiementStatus();
            countPaiementStatus.setFailed(status);
            countPaiementStatusRepository.save(countPaiementStatus);

            String error = "Paiement échoué.\n"+
                    "Pour toute réclamation, veuillez nous contacter au numéro figurant en bas de page.";

            redirectAttributes.addAttribute("messagePaiement", error);


            return new ModelAndView("redirect:/userpanel");
        }

        if(status.equals("PENDING")){

            CountPaiementStatus countPaiementStatus = new CountPaiementStatus();
            countPaiementStatus.setFailed(status);
            countPaiementStatusRepository.save(countPaiementStatus);


            return new ModelAndView("redirect:/userpanel");
        }



        return new ModelAndView("redirect:/userpanel");



    }

    /**
     * verify paiement premium
     * @param external_reference
     * @param status
     * @param amount
     * @param currency
     * @param operator
     * @param code
     * @param operator_reference
     * @param redirectAttributes
     * @return
     */

    @GetMapping("/verify_paiement_premium")
    public ModelAndView verify_user_paiement_premium(@RequestParam("external_reference") String external_reference,
                                             @RequestParam("status") String status,
                                             @RequestParam("amount") String amount,
                                             @RequestParam("currency") String currency,
                                             @RequestParam("operator") String operator,
                                             @RequestParam("code") String code,
                                             @RequestParam("operator_reference") String operator_reference,
                                             @RequestParam("extra_email" ) String email,
                                             RedirectAttributes redirectAttributes
    ){



        if(status.equals("SUCCESSFUL")){

            UserPaiement userPaiement = new UserPaiement();

            //String userN = external_reference.substring(8);

            User user = userRepository.findByEmail(email);

            if(user !=null){

                userPaiement.setUser_paiement(user);
                userPaiement.setAmount(amount);
                userPaiement.setCode(code);
                userPaiement.setCurrency(currency);
                userPaiement.setOperator(operator);
                userPaiement.setExternalReference(external_reference);
                userPaiement.setOperatorReference(operator_reference);
                userPaiement.setStatus(status);
                userPaiement.setPaySuccess(true);

                userPaiementRepository.save(userPaiement);

                //TODO: set the date of end of subscribe at 1 month. the number MAX of posts a 30
                User user1 = userRepository.findByEmail(user.getEmail());
                if(user1 != null){
                    //we set subscribe at true,
                    user1.setSubscribe(true);
                    user1.setNumberPostPermits(30);
                    user1.setTypeAccount(User.TypeAccount.PREMIUM);
                    user1.setExpireSubscribeDate(LocalDateTime.now().plusDays(30));

                    userRepository.save(user1);
                }

                CountPaiementStatus countPaiementStatus = new CountPaiementStatus();
                countPaiementStatus.setFailed(status);
                countPaiementStatusRepository.save(countPaiementStatus);

                String success = "Felicitations \n" +
                        "Votre paiement est pris en compte.";

                redirectAttributes.addAttribute("messagePaiement",success);

                return new ModelAndView("redirect:/userpanel");

            }


        }

        if(status.equals("FAILED")){

            CountPaiementStatus countPaiementStatus = new CountPaiementStatus();
            countPaiementStatus.setFailed(status);
            countPaiementStatusRepository.save(countPaiementStatus);

            String error = "Paiement échoué.\n"+
                    "Pour toute réclamation, veuillez nous contacter au numéro figurant en bas de page.";

            redirectAttributes.addAttribute("messagePaiement", error);


            return new ModelAndView("redirect:/userpanel");
        }

        if(status.equals("PENDING")){

            CountPaiementStatus countPaiementStatus = new CountPaiementStatus();
            countPaiementStatus.setFailed(status);
            countPaiementStatusRepository.save(countPaiementStatus);


            return new ModelAndView("redirect:/userpanel");
        }



        return new ModelAndView("redirect:/userpanel");



    }

    /**
     * verify paiement standard
     * @param external_reference
     * @param status
     * @param amount
     * @param currency
     * @param operator
     * @param code
     * @param operator_reference
     * @param redirectAttributes
     * @return
     */
    @GetMapping("/verify_paiement_standard")
    public ModelAndView verify_user_paiement_standard(@RequestParam("external_reference") String external_reference,
                                                     @RequestParam("status") String status,
                                                     @RequestParam("amount") String amount,
                                                     @RequestParam("currency") String currency,
                                                     @RequestParam("operator") String operator,
                                                     @RequestParam("code") String code,
                                                     @RequestParam("operator_reference") String operator_reference,
                                                     @RequestParam("extra_email") String email,
                                                     RedirectAttributes redirectAttributes
    ){



        if(status.equals("SUCCESSFUL")){

            UserPaiement userPaiement = new UserPaiement();

            //String userN = external_reference.substring(8);

            User user = userRepository.findByEmail(email);

            if(user !=null){

                userPaiement.setUser_paiement(user);
                userPaiement.setAmount(amount);
                userPaiement.setCode(code);
                userPaiement.setCurrency(currency);
                userPaiement.setOperator(operator);
                userPaiement.setExternalReference(external_reference);
                userPaiement.setOperatorReference(operator_reference);
                userPaiement.setStatus(status);
                userPaiement.setPaySuccess(true);

                userPaiementRepository.save(userPaiement);

                //TODO: set the date of end of subscribe at 1 month. the number MAX of posts a 30
                User user1 = userRepository.findByEmail(user.getEmail());
                if(user1 != null){
                    //we set subscribe at true,
                    user1.setSubscribe(true);
                    user1.setNumberPostPermits(30);
                    user1.setTypeAccount(User.TypeAccount.STANDARD);
                    user1.setExpireSubscribeDate(LocalDateTime.now().plusDays(30));

                    userRepository.save(user1);
                }

                CountPaiementStatus countPaiementStatus = new CountPaiementStatus();
                countPaiementStatus.setFailed(status);
                countPaiementStatusRepository.save(countPaiementStatus);

                String success = "Felicitations \n" +
                        "Votre paiement est pris en compte.";

                redirectAttributes.addAttribute("messagePaiement",success);

                return new ModelAndView("redirect:/userpanel");

            }


        }

        if(status.equals("FAILED")){

            CountPaiementStatus countPaiementStatus = new CountPaiementStatus();
            countPaiementStatus.setFailed(status);
            countPaiementStatusRepository.save(countPaiementStatus);

            String error = "Paiement échoué.\n"+
                    "Pour toute réclamation, veuillez nous contacter au numéro figurant en bas de page.";

            redirectAttributes.addAttribute("messagePaiement", error);


            return new ModelAndView("redirect:/userpanel");
        }

        if(status.equals("PENDING")){

            CountPaiementStatus countPaiementStatus = new CountPaiementStatus();
            countPaiementStatus.setFailed(status);
            countPaiementStatusRepository.save(countPaiementStatus);


            return new ModelAndView("redirect:/userpanel");
        }



        return new ModelAndView("redirect:/userpanel");



    }
    /**
     * here we have list table paiement
     * we can know if user has paid or not

     */

    @GetMapping("/list_users_paiement")
    public String  listUsersPaiement(Model model){

        List<UserPaiement> userList = userPaiementRepository.findAll();

        model.addAttribute("listUsers", userList);

        return "users_paiement_list";
    }


    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }





}
