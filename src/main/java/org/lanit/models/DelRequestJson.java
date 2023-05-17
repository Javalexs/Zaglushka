package org.lanit.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.lanit.deleteJson.Info;

public class DelRequestJson{

	@JsonProperty("lastUpdate")
	private String lastUpdate;

	@JsonProperty("delete")
	private Delete delete;

	@JsonProperty("uuid")
	private String uuid;

	@JsonProperty("info")
	private org.lanit.deleteJson.Info info;

	public void setLastUpdate(String lastUpdate){
		this.lastUpdate = lastUpdate;
	}

	public String getLastUpdate(){
		return lastUpdate;
	}

	public void setDelete(Delete delete){
		this.delete = delete;
	}

	public Delete getDelete(){
		return delete;
	}

	public void setUuid(String uuid){
		this.uuid = uuid;
	}

	public String getUuid(){
		return uuid;
	}

	public void setInfo(org.lanit.deleteJson.Info info){
		this.info = info;
	}

	public Info getInfo(){
		return info;
	}
}