package com.example.jay.shakunaku.Utils;

import android.os.Environment;

public class FilePaths {

    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";

    public String CAMERA = ROOT_DIR + "/DCIM/Camera";

    public String DOWNLOAD = ROOT_DIR + "/Download";

    public String WHATSAPP_IMAGES = ROOT_DIR + "/WhatsApp/Media/WhatsApp Images";

    public String WHATSAPP_PROFILE = ROOT_DIR + "/whatsApp/Media/WhatsApp Profile Photos";

    public String WHATSAPP_GIFS = ROOT_DIR + "/WhatsApp/Media/WhatsApp Animated Gifs";

    public String FIREBASE_IMAGE_STORAGE = "photos/users";

}
