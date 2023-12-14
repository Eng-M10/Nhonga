package dev.muvi.nhonga.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserFirebase {

    public static String getUserID(){

        FirebaseAuth auth = FirebaseConfig.getReferenceAuth();

        return auth.getCurrentUser().getUid();

    }

    public static FirebaseUser getCurrentUser(){
        FirebaseAuth user = FirebaseConfig.getReferenceAuth();

        return user.getCurrentUser();
    }

    public static boolean updateUserType(String type){
        try{
            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(type)
                    .build();
            user.updateProfile(profile);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }


        return true;
    }


}
