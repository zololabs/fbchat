package zolo.facebook.chat;

import java.io.PrintStream;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class FacebookChat
{
  private XMPPConnection connection;

  public FacebookChat(String apiKey, String accessToken)
    throws XMPPException
  {
    ConnectionConfiguration config = new ConnectionConfiguration("chat.facebook.com", 5222);
    config.setSASLAuthenticationEnabled(true);

    this.connection = new XMPPConnection(config);

    SmackConfiguration.setPacketReplyTimeout(15000);

    SASLAuthentication.registerSASLMechanism("X-FACEBOOK-PLATFORM", SASLXFacebookPlatformMechanism.class);
    SASLAuthentication.supportSASLMechanism("X-FACEBOOK-PLATFORM", 0);
    this.connection.connect();
    this.connection.login(apiKey, accessToken);
  }

  public void sendMessage(String userJID, String message)
    throws XMPPException
  {
    Chat chat = this.connection.getChatManager().createChat(userJID, new MessageListener() {
      public void processMessage(Chat chat, Message message) {
        System.out.println(chat.getParticipant() + " says:" + message.getBody());
      }
    });
    chat.sendMessage(message);
  }

  public void closeConnection()
  {
    this.connection.disconnect();
  }

  public boolean isLive() {
    return this.connection.isConnected();
  }

  public static void main(String[] args) {
    try {
      String access_token = "CAAFJL2RstUwBAEpOAlCQsjKAT3m3pc2UVpNr4ihVDIehPWnhlyVBXCJQ4u5KQkbP0HIvpytFfNLJSTq5AHaHGtMyeb6esgZA2xEhG2exaZB2cMZAmZBMKZAHcEkA4JXi5pc6SPZBZBDt3lEydgt4MWKTTA4KNMuz1EZD";
      FacebookChat facebookChat = new FacebookChat("361942873847116", access_token);
      facebookChat.sendMessage("-100003913475844@chat.facebook.com", "Thanks for that, getting this into ZoloLabs Github next...");
    } catch (XMPPException e) {
      e.printStackTrace();
    }
  }
}