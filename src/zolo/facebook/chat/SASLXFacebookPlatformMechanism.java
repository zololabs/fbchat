package zolo.facebook.chat;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import javax.security.sasl.Sasl;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.SASLMechanism.AuthMechanism;
import org.jivesoftware.smack.sasl.SASLMechanism.Response;
import org.jivesoftware.smack.util.Base64;

public class SASLXFacebookPlatformMechanism extends SASLMechanism
{
  public static final String NAME = "X-FACEBOOK-PLATFORM";
  private String apiKey = "";
  private String accessToken = "";

  public SASLXFacebookPlatformMechanism(SASLAuthentication saslAuthentication)
  {
    super(saslAuthentication);
  }

  protected void authenticate() throws IOException, XMPPException
  {
    getSASLAuthentication().send(new SASLMechanism.AuthMechanism(getName(), ""));
  }

  public void authenticate(String apiKey, String host, String accessToken) throws IOException, XMPPException
  {
    this.apiKey = apiKey;
    this.accessToken = accessToken;
    this.hostname = host;

    String[] mechanisms = { "DIGEST-MD5" };
    Map props = new HashMap();
    this.sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props, this);
    authenticate();
  }

  protected String getName()
  {
    return "X-FACEBOOK-PLATFORM";
  }

  public void challengeReceived(String challenge) throws IOException
  {
    byte[] response = null;

    if (challenge != null) {
      String decodedChallenge = new String(Base64.decode(challenge));
      Map parameters = getQueryMap(decodedChallenge);

      String version = "1.0";
      String nonce = (String)parameters.get("nonce");
      String method = (String)parameters.get("method");

      long callId = new GregorianCalendar().getTimeInMillis() / 1000L;

      String composedResponse = "api_key=" + URLEncoder.encode(this.apiKey, "utf-8") + "&call_id=" + callId + "&method=" + URLEncoder.encode(method, "utf-8") + "&nonce=" + URLEncoder.encode(nonce, "utf-8") + "&access_token=" + URLEncoder.encode(this.accessToken, "utf-8") + "&v=" + URLEncoder.encode(version, "utf-8");

      response = composedResponse.getBytes("utf-8");
    }

    String authenticationText = "";

    if (response != null) {
      authenticationText = Base64.encodeBytes(response, 8);
    }

    getSASLAuthentication().send(new SASLMechanism.Response(authenticationText));
  }

  private Map<String, String> getQueryMap(String query) {
    Map map = new HashMap();
    String[] params = query.split("\\&");

    for (String param : params) {
      String[] fields = param.split("=", 2);
      map.put(fields[0], fields.length > 1 ? fields[1] : null);
    }
    return map;
  }
}