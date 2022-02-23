//package org.techtown.newandroid;
//
//import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;
//
//import android.app.AlertDialog;
//import android.content.ClipData;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.media.Image;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.PersistableBundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.NonUiContext;
//import androidx.annotation.Nullable;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.sql.Array;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.WeakHashMap;
//
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.RequestBody;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//
//public class TestUploadImage {
//    //이미지 뷰 5장
//    ImageView image1, image2, image3, image4, image5;
//
//    //이미지 업로드를 위해 상속한 인터페이스
//    ApiService apiService;
//    //유저 아이디 가져오기 위함
//    User user;
//    String user_id;
//    private ArrayList<String> permissionsToRequest;
//    private ArrayList<String> permissionRejected = new ArrayList<>();
//    private ArrayList<String> permissions = new ArrayList<>();
//    private final static int ALL_PERMISSIONS_RESULT = 107;
//    private final static int IMAGE_RESULT = 200;
//    public final static int REQUEST_CODE = 1;
//
//    //FloatingActionButton fabUpload;
//    //Bitmap[] mBitmap = new Bitmap[5];
//    ArrayList<Bitmap> mBitmap = new ArrayList<>();
//    TextView textView;
//
//    Button fabUpload, fab;
//
//    Uri picUri;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test_upload_image);
//
//        askPermission();
//        initRetrofitClient();
//
//        image1 = (ImageView)findViewById(R.id.imageView1);
//        image2 = (ImageView)findViewById(R.id.imageView2);
//        image3 = (ImageView)findViewById(R.id.imageView3);
//        image4 = (ImageView)findViewById(R.id.imageView4);
//        image5 = (ImageView)findViewById(R.id.imageView5);
//
//        fabUpload = findViewById(R.id.fabUpload);
//        textView = findViewById(R.id.textView);
//        fab = findViewById(R.id.fab);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
//                getPickImageChooserIntent();
//            }
//        });
//
//        fabUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mBitmap != null) {
//                    for (int i = 0; i < mBitmap.size(); i++)
//                        multipartImageUpload(i);
//                } else {
//                    Toast.makeText(getApplicationContext(), "Bitmap is null. Try again", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        final PrefManager prefManager = PrefManager.getInstance(TestUploadImage.this);
//        user = prefManager.getUser();
//
//        if(prefManager.isLoggedIn()) {
//            user_id = String.valueOf(user.getUser_id());
//        }
//
//        //initRetrofitClient();
//    }
//
//    private void askPermission() {
//        permissions.add(WRITE_EXTERNAL_STORAGE);
//        permissions.add(READ_EXTERNAL_STORAGE);
//        permissionsToRequest = findUnAskedPermissions(permissions);
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(permissionsToRequest.size() > 0)
//                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
//        }
//    }
//
//    private void initRetrofitClient() {
//        OkHttpClient client = new OkHttpClient.Builder().build();
//
//        apiService = new Retrofit.Builder().baseUrl(URL_UPLOAD).client(client).build().create(ApiService.class);
//    }
//
//    //이미지 가져오기
//    public void getPickImageChooserIntent() {
//        List<Intent> allIntents = new ArrayList<>();
//        PackageManager packageManager = getPackageManager();
//        ArrayList<Image> images = new ArrayList<>();
//        //Intent intent = new Intent(Intent.ACTION_PICK);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        //intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        startActivityForResult(Intent.createChooser(intent, "이미지 다중 선택"), REQUEST_CODE);
//    }
//
//    //이미지 촬영 후 Uri
//    private Uri getCaptuerImageOutputUri() {
//        Uri outputFileUri = null;
//        File getImage = getExternalFilesDir("");
//        if(getImage != null) {
//            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
//        }
//        return outputFileUri;
//    }
//
//    //getPickImageChooserIntent()에서 startActivitiForResult 하고 난 결과를 받는 메소드
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        String imagePath = null;
//        ArrayList<String> imageListUri = new ArrayList<>();
//        ArrayList<Uri> realUri = new ArrayList<>();
//
//        if(requestCode = REQUEST_CODE) {
//            Toast.makeText(this, "여기 드가욤", Toast.LENGTH_SHORT).show();
//            if(data.getClipData() == null) {
//                Toast.makeText(this, "다중 선택이 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
//            } else { //다중 선택했을 경우
//                ClipData clipData = data.getClipData();
//                //Log.i("clipdata1 : ", String.valueOf(clipData.getItemCount()));
//
//                if(clipData.getItemCount() > 5) {
//                    Toast.makeText(TestUploadImage.this, "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
//                } else if (clipData.getItemCount() == 1) {
//                    Uri tempUri;
//                    tempUri = clipData.getItemAt(0).getUri();
//                    imagePath = tempUri.toString();
//
//                    mBitmap.add(BitmapFactory.decodeFile(imagePath));
//                    image1.setImageBitmap(mBitmap.get(0));
//                    //File file = new File(imagePath);
//                    //imageView1.setImageURI(Uri.fromFile(file));
//                } else if ((clipData.getItemCount() > 1) && (clipData.getItemCount() <=5)) {
//                    for (int i = 0; i < clipData.getItemCount(); i++) {
//                        Uri tempUri;
//                        tempUri = clipData.getItemAt(i).getUri();
//                        Log.i("temp: ", i + " " + tempUri.toString());
//                        imageListUri.add(tempUri.toString());
//                        realUri.add(tempUri);
//                        //mBitmap.add(i, BitmapFactory.decodeFile(imageListUri.get(i)));
//
//                        try {
//                            mBitmap.add(i, BitmapFactory.decodeStream(getContentResolver().openInputStream(realUri.get(i))));
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    if(clipData.getItemCount() == 2) {
//                        image1.setImageBitmap(mBitmap.get(0));
//                        image2.setImageBitmap(mBitmap.get(1));
//                    } if(clipData.getItemCount() == 3) {
//                        image1.setImageBitmap(mBitmap.get(0));
//                        image2.setImageBitmap(mBitmap.get(1));
//                        image3.setImageBitmap(mBitmap.get(2));
//                    } if(clipData.getItemCount() == 4) {
//                        image1.setImageBitmap(mBitmap.get(0));
//                        image2.setImageBitmap(mBitmap.get(1));
//                        image3.setImageBitmap(mBitmap.get(2));
//                        image4.setImageBitmap(mBitmap.get(3));
//                    } if(clipData.getItemCount() == 5) {
//                        image1.setImageBitmap(mBitmap.get(0));
//                        image2.setImageBitmap(mBitmap.get(1));
//                        image3.setImageBitmap(mBitmap.get(2));
//                        image4.setImageBitmap(mBitmap.get(3));
//                        image5.setImageBitmap(mBitmap.get(4));
//                    }
//                }
//            }
//        }
//    }
//
//    private String getImageFromFilePath(Intent data) {
//        return getPathFromUri(data.getData());
//    }
//
//    public String getImageFilePath(Intent data) {
//        return getImageFromFilePath(data);
//    }
//
//    private String getPathFromUri(Uri contetnUri) {
//        String[] proj = {MediaStore.Audio.Media.DATA};
//        Cursor cursor = getContentResolver().query(contetnUri, proj, null, null, null);
//        int column_index = cursor.getcolumnIndexOfThrow(MediaStore.Audio.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//
//        outState.putParcelable("pic_uri", picUri);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        picUri = savedInstanceState.getParcelable("pic_uri");
//    }
//
//    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
//        ArrayList<String> result = new ArrayList<String>();
//
//        for(String perm : wanted) {
//            if(IhasPermissions(perm)) {
//                result.add(perm);
//            }
//        }
//        return result;
//    }
//
//    private boolean hasPermissions(String permissions) {
//        if(canMakeSmores()) {
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                return (checkSelfPermission(permissions) == PackageManager.PERMISSION_GRANTED);
//            }
//        }
//        return true;
//    }
//
//    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(this)
//                .setMessage(message)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
//    }
//
//    private boolean canMakeSmores() {
//        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
//    }
//
//    @TartgetApi(Build.VERSION_CODES.M)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch(requestCode) {
//            case ALL_PERMISSIONS_RESULT:
//                for(String perms : permissionsToRequest) {
//                    if(!hasPermissions(perms)) {
//                        permissionsRejected.add(perms);
//                    }
//                }
//
//                if(permissionsRejected.size() > 0) {
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
//                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
//                                        }
//                                    });
//                            return;
//                        }
//                    }
//                }
//                break;
//        }
//    }
//
//    //실제 이미지를 업로드 하는 파트
//    //여기서 파일의 이름을 바꿔야 함
//    private void multipartImageUpload(int index) {
//        try {
//            File filesDir = getApplicationContext().getFilesDir();
//            //여기서 png 앞에를 유저 id + 레지스터 넘버 이런식으로 바꿀 것
//            File file = new File(filesDir, filesDir.getName() + ".png");
//
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            mBitmap.get(index).compress(Bitmap.CompressFormat.PNG, 0, bos);
//            byte[] bitmapdata = bos.toByteArray();
//
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(bitmapdata);
//            fos.flush();
//            fos.close();
//
//            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
//            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", title.getName(), reqFile);
//            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");
//
//            Call<ResponseBody> req = apiService.postImage(body, name);
//
//            req.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    if(response.code() == 200) {
//                        textView.setText("uploaded success");
//                        textView.setTextColor(Color.BLUE);
//                    }
//
//                    Toast.makeText(getApplicationContext(), response.code() + "", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    textView.setText("uploaded fail");
//                    textView.setTextColor(Color.RED);
//                    Toast.makeText(getApplicationContext(), "req fail", Toast.LENGTH_SHORT).show();
//                    t.printStackTrace();
//                }
//            });
//        } catch(FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
