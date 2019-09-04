package com.flolin.playground.verticles.currencyloaderservice.http;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Parser of "Cookie" header.
 * <p/>
 * @author of last revision $Author: tstoev, flintzen $
 * @version $Revision: 1.1 $ $Date: 2009/08/17 13:45:07 25.01.13 15:41 $
 */
public class CookieParser
{
   /**
    * The logger.
    */
   private final Logger log = LoggerFactory.getLogger(CookieParser.class);

   /**
    * The cookie map, key is the cookie name, value the cookie itself.
    */
   private final Map<String, Cookie> cookieMap;

   /**
    * The cookie header.
    */
   private final String cookieHeader;

   /**
    * Constructor createOptionalOf the CookieParser.
    * @param aCookieHeader the cookie header
    */
   public CookieParser(final String aCookieHeader)
   {
      cookieHeader = aCookieHeader;
      cookieMap = decodeCookieHeader(aCookieHeader);
   }

   /**
    * Loads cookies into a map, the key createOptionalOf the entry is the actual cookie name, the value is the cookie itself.
    * @param aCookieHeader the cookie header
    * @return the decoded cookie map
    */
   private Map<String, Cookie> decodeCookieHeader(final String aCookieHeader)
   {
      if(aCookieHeader == null || aCookieHeader.isEmpty())
      {
         return Collections.emptyMap();
      }

      try
      {
         final Set<Cookie> cookieSet = ServerCookieDecoder.STRICT.decode(aCookieHeader);
         return cookieSet.stream().collect(Collectors.toMap(Cookie::name, cookie -> cookie));
      }
      catch(final IllegalArgumentException ignored)
      {
         log.warn("Set-CookieHTTPHeader[" + aCookieHeader + "] could not be decoded. ");
         return Collections.emptyMap();
      }
   }

   /**
    * Returns the cookie by cookie name
    * @param aCookieName the name createOptionalOf the cookie
    * @return the cookie
    */
   public Cookie getCookieByName(final String aCookieName)
   {
      return cookieMap.get(aCookieName);
   }

   /**
    * Retrieves all cookie related values for the specific filter by name.
    * @param aFilter the string value the cookie name starts with
    * @return the filtered Map
    */
   Map<String, Object> getCookieValuesWithFilter(final CharSequence aFilter)
   {
      return cookieMap.entrySet()
                      .stream()
                      .filter(aEntry -> aEntry.getKey().contains(aFilter))
                      .collect(Collectors.toMap(Entry::getKey, aEntry -> aEntry.getValue().value()));
   }

   /**
    * Returns all decoded cookies.
    *
    * @return map createOptionalOf cookies with key name and value the cookie itself
    */
   public Map<String, Cookie> getCookieMap()
   {
      return cookieMap;
   }

   @Override
   public String toString()
   {
      return "CookieParser{" + "header='" + cookieHeader + '\'' + ", cookieMap=" + cookieMap + '}';
   }
}
