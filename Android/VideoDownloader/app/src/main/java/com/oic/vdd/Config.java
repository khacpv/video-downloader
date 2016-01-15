package com.oic.vdd;

/**
 * Created by khacpham on 1/8/16.
 */
public class Config {
    public enum BUILD_MODE {
        PRODUCT("product"), DEVELOP("develop");

        private String value;

        BUILD_MODE(String value){
            this.value = value;
        }

        public String getValue(){
            return value;
        }
    }

    public static BUILD_MODE MODE = BUILD_MODE.PRODUCT;
}
