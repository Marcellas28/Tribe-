package com.dayworks_ltd.loyalty_engine.payments.utils;

import com.dayworks_ltd.loyalty_engine.payments.models.ConfirmPaymentRequest;
import com.dayworks_ltd.loyalty_engine.payments.models.ConfirmPaymentResponse;
import com.dayworks_ltd.loyalty_engine.payments.models.IntiatePaymentRequest;
import com.dayworks_ltd.loyalty_engine.payments.models.IntiatePaymentResponse;
import com.dayworks_ltd.loyalty_engine.payments.utils.dto.*;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Objects;

@Slf4j
@Service
public class MpesaUtils {

    @Value("${mpesa.consumersecret}")
    private String consumerSecret;

    @Value("${mpesa.consumerkey}")
    private String consumerKey;

    @Value("${mpesa.passkey}")
    private String passKey;

    @Value("${mpesa.auth-url}")
    private String authUrl;

    @Value("${mpesa.callback-url}")
    private String callbackUrl;

    @Value("${mpesa.initiate-url}")
    private String initiateUrl;

    @Value("${mpesa.confirm-url}")
    private String confirmUrl;

    @Value("${mpesa.shortcode}")
    private String shortCode;

    @Value("${mpesa.tillnumber}")
    private String tillNumber;



    private final Gson gson = new Gson();

    private final OkHttpClient client = new OkHttpClient();

    private TokenResponse getToken (){
        String authString = consumerKey + ":" + consumerSecret;
        String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
        OkHttpClient client = new OkHttpClient();
        TokenResponse tokenResponse = new TokenResponse();

        HttpUrl encodedUrl =  Objects.requireNonNull(HttpUrl.parse(authUrl))
                .newBuilder()
                .addQueryParameter("grant_type", "client_credentials")
                .build();

        log.info("**********************");
        log.info(encodedUrl.toString());
        log.info(encodedAuthString);
        log.info("**********************");


        Request request =  new Request.Builder()
                .url(encodedUrl)
                .get()
                .addHeader("Authorization" ,"Basic " + encodedAuthString)
                .build();

        try (Response response = client.newCall(request).execute()){
            if (response.isSuccessful() && response.body() != null){
                String responseBody = response.body().string();
                log.info("the token is" + responseBody);
                tokenResponse = gson.fromJson(responseBody, TokenResponse.class);
            } else {
               log.info("failed to get token with error" + response.body().string());
            }
        } catch (Exception e) {
           log.info("Error when authenticating" + e.getLocalizedMessage());
        }

        return tokenResponse;
    }

    private String getPassword(){
        String timeStamp = CommonUtils.formatedDateTime();
        String password =  shortCode + passKey +  timeStamp;
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    public IntiatePaymentResponse intiatePayment (IntiatePaymentRequest intiatePaymentRequest){



        IntiatePaymentResponse iPR = new IntiatePaymentResponse();

        IntiatePaymentDto intiatePaymentDto = IntiatePaymentDto.builder()
                .businessShortCode(shortCode)
                .password(getPassword())
                .partyB(tillNumber)
                .partyA(intiatePaymentRequest.getPhoneNumber())
                .phoneNumber(intiatePaymentRequest.getPhoneNumber())
                .amount(intiatePaymentRequest.getAmount())
                .callbackUrl(callbackUrl)
                .timeStamp(CommonUtils.formatedDateTime())
                .accountReference("AtmA")
                .transactionDesc("payment for milk")
                .transactionType("CustomerBuyGoodsOnline")
                .build();

        String initiatePaymentDtoStr = gson.toJson(intiatePaymentDto);
        RequestBody body = RequestBody.create(initiatePaymentDtoStr, MediaType.get("application/json"));

        String token = getToken().getAccessToken();
        System.out.println("the token recieved is " + token);
        Request request = new Request.Builder()
                .url(initiateUrl)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()){
            if (response.isSuccessful() && response.body() != null){
                String resp = response.body().string();
                InitPaymentRespDto initPaymentRespDto = gson.fromJson(resp, InitPaymentRespDto.class);

                iPR.setMessage(initPaymentRespDto.getCustomerMessage());
                iPR.setStatus(initPaymentRespDto.getResponseCode());
                iPR.setCheckoutRequestID(initPaymentRespDto.getCheckoutRequestID());

            } else {
                System.out.println("****"+response.body().string());
                iPR.setMessage("could not initiate payment");
                iPR.setStatus("500");
            }

        } catch (Exception e) {
            System.out.println("Error when initiating payment");
        }
        return iPR;
    }

    public ConfirmPaymentResponse confirmPayment(ConfirmPaymentRequest confirmPaymentRequest) {
        ConfirmPaymentResponse cpr = new ConfirmPaymentResponse();

        ConfirmPaymentDto cpd = ConfirmPaymentDto.builder()
                .businessShortCode(shortCode)
                .timestamp(CommonUtils.formatedDateTime())
                .password(getPassword())
                .checkoutRequestID(confirmPaymentRequest.getCheckoutRequestID())
                .build();

        String cpdStr = gson.toJson(cpd);
        RequestBody body = RequestBody.create(cpdStr, MediaType.get("application/json"));

        String token = getToken().getAccessToken();
        System.out.println("the token recieved is " + token);
        Request request = new Request.Builder()
                .url(confirmUrl)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()){
            if (response.isSuccessful()){
                System.out.println("confirm sucess");
                String respBody = response.body().string();
                ConfirmPaymentRespDto cprd = gson.fromJson(respBody, ConfirmPaymentRespDto.class);
                cpr.setStatus(cprd.getResultCode());
                cpr.setMessage(cprd.getResultDesc());
            } else {
                System.out.println("confirm fail");
                String respBody = response.body().string();
                System.out.println("respbody: " + respBody);
                ConfirmPaymentRespDto cprd = gson.fromJson(respBody, ConfirmPaymentRespDto.class);
                cpr.setStatus(cprd.getResultCode());
                cpr.setMessage(cprd.getResultDesc());
            }
        } catch (Exception e) {
            System.out.println("Error - Could not make request");
        }
        return cpr;
    }
}
