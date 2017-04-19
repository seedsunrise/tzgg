package cn.com.flaginfo.service.impl;



import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * *<p>Title:json格式化null为“”</p>* 
	<p>Description:自动转化时间为yyyy-mm-dd hh：mm：ss </p>* 
     @author liming
     @date 2016年8月9日
 */
@Component("customObjectMapper")
public class CustomObjectMapper extends ObjectMapper {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public CustomObjectMapper() {
        SimpleModule module = new SimpleModule();
      //  module.addSerializer(Date.class,new ItemSerializer());
        this.registerModule(module);
        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(
                    Object value,
                    JsonGenerator jg,
                    SerializerProvider sp) throws IOException {
                jg.writeString("");
            }
        });
    }

	
//	class ItemSerializer extends JsonSerializer<Date>{
//
//		@Override
//		public void serialize(Date value, JsonGenerator gen,
//				SerializerProvider serializers) throws IOException,
//				JsonProcessingException {
//			gen.writeString(sdf.format(value));
//		}
//		
//		
//	}
   
}
