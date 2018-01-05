package fpt.isc.nshreport.models.objectParse;

public class UploadDocument {
	public Result Result;

	public class Result {
		public int ErrorCode;
		public String ErrorDescription;
		public Data Data;

		public class Data {
			public String file_name;
			public int size;
			public String link;
		}

	}

}
