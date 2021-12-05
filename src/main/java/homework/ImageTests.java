package homework;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ImageTests extends BaseTest {
    private final String PATH_TO_IMAGE = "src/main/resources/horse.jpeg";
    //public final String PATH_TO_URL = "https://avatars.mds.yandex.net/get-zen_doc/4389079/pub_60edb2a57fcf3478637de0d8_60edb2f30af4417499b65765/scale_1200";
    //public final String PATH_TO_VIDEO = "src/main/resources/video.mp4";

    static String encodedFile;
    String uploadedImageId = given()
            .header("Authorization", token)
            .multiPart("image", new File(PATH_TO_IMAGE))
            .expect()
            .statusCode(200)
            .when()
            .post("https://api.imgur.com/3/image")
            .prettyPeek()
            .then()
            .extract()
            .response()
            .jsonPath()
            .getString("data.deletehash");



    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
    }
    @Test
    void uploadFileTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

    }

    @Test
    void uploadFileImageTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/main/resources/horse.jpeg"))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }
    @Test
    void uploadGifFileTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/main/resources/heart.gif"))
                .expect()
                .statusCode(200)
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .body("data.width", equalTo(600))
                .body("data.height", equalTo(480))
                .body("data.type", equalTo("image/gif"))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }
    @Test
    void getImage() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/main/resources/horse.jpeg"))
                .expect()
                .body("success", is(true))
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image")
                .then()
                .extract()
                .jsonPath()
                .getString("data.deletehash");
    }

//    @Test
//    void uploadUrlImageTest() {
//        uploadedImageId = given()
//                .headers("Authorization", token)
//                .multiPart("image", PATH_TO_URL)
//                .expect()
//                .statusCode(200)
//                .body("success", is(true))
//                .body("data.id", is(notNullValue()))
//                .body("ad_url", equalTo(""))
//                .body("name", equalTo(""))
//                .when()
//                .post("https://api.imgur.com/3/image")
//                .prettyPeek()
//                .then()
//                .extract()
//                .response()
//                .jsonPath()
//                .getString("data.deletehash");
//    }
//    @Test
//    void uploadBigFileTest() {
//        uploadedImageId = given()
//                .headers("Authorization", token)
//                .multiPart("image", new File("src/main/resources/test_photo.jpg"))
//                .expect()
//                .statusCode(200)
//                .body("success", is(true))
//                .body("data.id", is(notNullValue()))
//                .body("data.size", lessThan(12242944))
//                .body("data.animated",equalTo(false))
//                .when()
//                .post("https://api.imgur.com/3/image")
//                .prettyPeek()
//                .then()
//                .extract()
//                .response()
//                .jsonPath()
//                .getString("data.deletehash");
//    }
//    @Test
//    void uploadFileVideoTest() {
//        uploadedImageId = given()
//                .header("Authorization", token)
//                .multiPart("video", new File(PATH_TO_VIDEO))
//                .expect()
//                .statusCode(200)
//                .when()
//                .post("https://api.imgur.com/3/upload")
//                .prettyPeek()
//                .then()
//                .extract()
//                .response()
//                .jsonPath()
//                .getString("data.deletehash");
//    }
//
//
//
//    @Test
//    void favoriteImageTest() {
//        given()
//                .headers("Authorization", token)
//                .expect()
//                .body("success", is(true))
//                .when()
//                .post("https://api.imgur.com/3/image/{{imageHash}}/favorite", uploadedImageId)
//                .then()
//                .statusCode(200);
//    }
//    @Test
//    void favoriteVideoTest() {
//        given()
//                .headers("Authorization", token)
//                .expect()
//                .body("success", is(true))
//                .when()
//                .post("https://api.imgur.com/3/image/{{imageHash2}}/favorite", uploadedImageId)
//                .then()
//                .statusCode(200);
//    }

    @AfterEach
    void tearDown() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", "testprogmath", uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
    private byte[] getFileContent() {
        byte[] byteArray = new  byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(PATH_TO_IMAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}
