package com.superkele.demo.config;


import com.superkele.translation.extension.executecallback.CallBackRegister;
import com.superkele.translation.extension.executecallback.TranslateExecuteCallBack;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CallbackConfig {


    @Bean
    public CallBackRegister printerResCallbackRegister() {
        return new CallBackRegister() {
            @Override
            public String match() {
                return ".*";
            }

            @Override
            public TranslateExecuteCallBack callBack() {
                return res -> System.out.println(res);
            }
        };
    }

    @Bean
    public CallBackRegister callbackRegister() {
        return new CallBackRegister() {
            @Override
            public String match() {
                return "getUser";
            }

            @Override
            public TranslateExecuteCallBack callBack() {
                return res ->
                        System.out.println("getUser执行完毕，触发回调");
            }
        };
    }
}
