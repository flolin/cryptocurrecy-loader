package com.flolin.playground.verticles.currencyloaderservice.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Mapping class for the POST JSON payload.
 */
@SuppressWarnings({"FieldMayBeFinal", "JavaDoc", "WeakerAccess"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostPayload
{

   public PostPayload()
   {
   }


   public void validate()//throws InvalidRequestException
   {
       // throw some suitable Exception.
   }
}
