package shift.shift;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jesse on 4-7-2017.
 *
 * GA ALS VOLGT TE WERK:
 * Als je vanuit een andere klasse een alert wilt maken, roep hem dan als volgt aan:
 * CreateAlert.createAlert()
 *
 * Je hebt hierbij 2 opties als Parameters, deze zijn als volgt
 * String + Context --> Geef hier een string mee die je hier beneden kan bewerken
 * String + Context + Boolean --> Hiermee ga je ook iemand doorsturen naar een ander menu. Hierbij is er geen OK knop. Let op! Voeg hierbij wel de DelayCode() code toe. Deze is onder de pagina te vinden
 */

public class CreateAlert extends AppCompatActivity {

    public static void createAlert(String content, Context context) {
        /**
         * Hier kunnen alle verschillende dialogs veranderd worden. De content is de text in de dialog, de title de titel (duh :P)
         */
        boolean succes = false;
        String title = "Titel";

        if (content.equals("Niet_ingevuld")) {
            content = "Niet alle velden zijn correct ingevuld, probeer het opnieuw";
            title = "Velden Incompleet";
        } else if (content.equals("Niet_gelijk")) {
            content = "Zorg er voor dat beide wachtwoorden gelijk zijn!";
            title = "Wachtwoord Fout";
        } else if (content.equals("Geen_email")) {
            content = "Dit is geen juist e-mailadres. Controleer deze en probeer het opnieuw.";
            title = "Email Fout";
        } else if (content.equals("Cat_fail")) {
            content = "Er heeft zich een fout voorgedaan, kijk goed of u alle velden heeft ingevuld";
            title = "Foutmelding";
        } else if(content.equals("Team_fail")) {
            content = "Er heeft zich een fout voorgedaan, kijk goed of u alle velden heeft ingevuld";
            title = "Foutmelding";
        } else if(content.equals("Login")) {
          content = "Er is helaas een fout opgetreden. Controleer uw inloggegevens en probeer het opnieuw.";
            title = "Foutmelding";
        } else {
            content = "Er is iets onbekends mis gegaan. Contacteer de administrator voor toelichting";
            title = "Onbekende Fout";
        }


        final Context finalContext = context;
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        final boolean finalSucces = succes;
        builder.setTitle(title)
                .setMessage(content)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (finalSucces == false) {
                            //do stuff kan niks nu rip...  :/
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Deze gebruik je wanneer je geen OK knop wilt hebben en iemand wilt doorsturen naar een ander scherm.
     * @param content
     * @param context
     * @param geenOK
     */
    public static void createAlert(String content, Context context, boolean geenOK) {
        /**
         * Hier kunnen alle verschillende dialogs veranderd worden. De content is de text in de dialog, de title de titel (duh :P)
         */
        String title = "Titel";

        if (content.equals("Gelukt")) {
            content = "Registratie is gelukt! Uw wordt doorgestuurd naar het login menu";
            title = "Succesvol!";
        } else if(content.equals("Gelukt_cat")) {
            content = "Uw categorie is succesvol toegevoegd! Uw wordt doorgestuurd naar het hoofdmenu.";
            title = "Categorie Toegevoegd!";
        } else if(content.equals("Gelukt_team")) {
            content = "Uw team is succesvol toegevoegd! Uw wordt doorgestuurd naar het hoofdmenu.";
            title = "Team Toegevoegd!";
        }

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(title)
                .setMessage(content)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}


/**
 * De delay code
 */

//    private void delayCode() {
//        int WELCOME_TIMEOUT = 4000;
//
//        final Context context = this;
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent welcome = new Intent(context, Login.class);
//                startActivity(welcome);
//                finish();
//            }
//        },WELCOME_TIMEOUT);
//    }