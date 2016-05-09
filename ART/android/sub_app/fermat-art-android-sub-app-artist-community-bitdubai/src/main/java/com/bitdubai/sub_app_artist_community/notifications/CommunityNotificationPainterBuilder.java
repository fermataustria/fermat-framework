package com.bitdubai.sub_app_artist_community.notifications;

import com.bitdubai.fermat_android_api.engine.NotificationPainter;
import com.bitdubai.fermat_android_api.layer.definition.wallet.abstracts.AbstractFermatSession;
import com.bitdubai.fermat_art_api.layer.actor_connection.artist.enums.ArtistActorConnectionNotificationType;
import com.bitdubai.fermat_art_api.layer.sub_app_module.community.artist.interfaces.ArtistCommunityInformation;
import com.bitdubai.fermat_art_api.layer.sub_app_module.community.artist.interfaces.ArtistCommunitySubAppModuleManager;
import com.bitdubai.sub_app.artist_community.R;
import com.bitdubai.sub_app_artist_community.sessions.ArtistSubAppSession;

import java.util.List;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 05/04/16.
 */
public class CommunityNotificationPainterBuilder {
    public static NotificationPainter getNotification(
            final String code,
            AbstractFermatSession session) {
        NotificationPainter notification = null;
        int lastListIndex;
        if(session!=null){
            ArtistSubAppSession artistSubAppSession = (ArtistSubAppSession) session;
            ArtistCommunitySubAppModuleManager moduleManager = artistSubAppSession.getModuleManager();
            try{
                ArtistActorConnectionNotificationType notificationType = ArtistActorConnectionNotificationType.getByCode(code);

                switch (notificationType) {
                    case CONNECTION_REQUEST_RECEIVED:
                        List<ArtistCommunityInformation> artistPendingList= moduleManager.
                                listArtistsPendingLocalAction(
                                        moduleManager.getSelectedActorIdentity(),
                                        20,
                                        0);
                        lastListIndex = artistPendingList.size()-1;
                        ArtistCommunityInformation artistCommunityInformation = artistPendingList.get(
                                lastListIndex);
                        notification = new CommunityNotificationPainter(
                                "Artist Community",
                                "A Fermat Artist, "+artistCommunityInformation.getAlias()+", " +
                                        "wants to connect with you.",
                                "",
                                "",
                                R.drawable.aac_ic_nav_connections);
                        break;
                    case ACTOR_CONNECTED:
                        List<ArtistCommunityInformation> artistConnectedList= moduleManager.listAllConnectedArtists(
                                        moduleManager.getSelectedActorIdentity(),
                                        20,
                                        0);
                        lastListIndex = artistConnectedList.size()-1;
                        ArtistCommunityInformation artistConnectedInformation = artistConnectedList.get(
                                lastListIndex);
                        notification = new CommunityNotificationPainter(
                                "Artist Community",
                                "A Fermat Artist, "+artistConnectedInformation.getAlias()+", accepts" +
                                        " your connection request.",
                                "",
                                "",
                                R.drawable.aac_ic_nav_connections);
                        break;

                }
            } catch (Exception e) {
                //TODO: improve this catch
                e.printStackTrace();
            }
        }else{
            try {
                ArtistActorConnectionNotificationType notificationType = ArtistActorConnectionNotificationType.getByCode(code);

                switch (notificationType) {
                    case CONNECTION_REQUEST_RECEIVED:
                        notification = new CommunityNotificationPainter(
                                "Artist Community",
                                "A Fermat Artist wants to connect with you.",
                                "",
                                "",
                                R.drawable.aac_ic_nav_connections);
                        break;
                    case ACTOR_CONNECTED:
                        notification = new CommunityNotificationPainter(
                                "Artist Community",
                                "A Fermat Artist accept your connection request.",
                                "",
                                "",
                                R.drawable.aac_ic_nav_connections);
                        break;

                }
            } catch (Exception e) {
                //TODO: improve this catch
                e.printStackTrace();
            }
        }
        return notification;
    }
}
