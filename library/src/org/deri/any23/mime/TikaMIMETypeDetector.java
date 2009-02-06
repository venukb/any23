package org.deri.any23.mime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.jdom.JDOMException;


@SuppressWarnings("serial")
public class TikaMIMETypeDetector implements MIMETypeDetector {
	private static final String RESOURCE_NAME="org/deri/any23/mime/tika-config.xml";
	private static TikaConfig _config = null;
	private static MimeTypes _types;
	
	public TikaMIMETypeDetector(){
		InputStream is = getResourceAsStream();
		if(_config == null)
			try {
				_config = new TikaConfig(is);
			} catch (JDOMException e) {
				e.printStackTrace(System.err);
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		if(_types == null)
		_types = _config.getMimeRepository();
	}
	
	/**
	 * load tika configuration file
	 * @return 
	 */
	private InputStream getResourceAsStream() {
		InputStream result = null;
		System.out.println(TikaMIMETypeDetector.class.getResource("."));
	    result = TikaMIMETypeDetector.class.getResourceAsStream(RESOURCE_NAME);
	    if (result == null) {
	    	result = TikaMIMETypeDetector.class.getClassLoader().getResourceAsStream(RESOURCE_NAME);
	    	if (result == null) {
	    		result = ClassLoader.getSystemResourceAsStream(RESOURCE_NAME);
	    	}
	    }
	    if(result == null) {
	    	try {
	    		result = new FileInputStream(new File("src/"+RESOURCE_NAME));
	    		System.out.println(new File("src/"+RESOURCE_NAME).getAbsolutePath());
			} catch (FileNotFoundException e) {
				e.printStackTrace(System.err);
			}
//			
	    }
	    System.out.println(result);
	    return result;
	}
	
	public MIMEType guessMIMEType(String fileName, InputStream input,
			MIMEType mimeTypeFromMetadata) {

		Metadata meta = new Metadata();
		if(mimeTypeFromMetadata != null)
			meta.set(Metadata.CONTENT_TYPE, mimeTypeFromMetadata.getFullType());
		if(fileName != null)
			meta.set(Metadata.RESOURCE_NAME_KEY, fileName);
		
		String type = MimeTypes.DEFAULT;
		try {
			 MimeType mt = getMimeType(input, meta);
			if(mt != null) type = mt.toString(); 
				
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		return MIMEType.parse(type.toString());
	}

    /**
     * Automatically detects the MIME type of a document based on magic
     * markers in the stream prefix and any given metadata hints.
     * <p>
     * The given stream is expected to support marks, so that this method
     * can reset the stream to the position it was in before this method
     * was called.
     *
     * @param stream document stream
     * @param metadata metadata hints
     * @return MIME type of the document
     * @throws IOException if the document stream could not be read
     */
    private MimeType getMimeType(InputStream stream, final Metadata metadata) throws IOException {

        stream.mark(_types.getMinLength());
        try {
            byte[] prefix = getPrefix(stream, _types.getMinLength());
            MimeType type = _types.getMimeType(prefix);
            if (type != null) {
                return type;
            }
            
        } finally {
            stream.reset();
        }

        // Get type based on resourceName hint (if available)
        String resourceName = metadata.get(Metadata.RESOURCE_NAME_KEY);
        if (resourceName != null) {
            MimeType type = _types.getMimeType(resourceName);
            if (type != null) {
                return type;
            }
        }

        // Get type based on metadata hint (if available)
        String typename = metadata.get(Metadata.CONTENT_TYPE);
        if (typename != null) {
            try {
                return _types.forName(typename);
            } catch (MimeTypeException e) {
                ;// Malformed type name, ignore
            }
        }

        // Finally, use the default type if no matches found
        try {
            return _types.forName(MimeTypes.DEFAULT);
        } catch (MimeTypeException e) {
            ;// Should never happen
            return null;
        }
    }

   private byte[] getPrefix(InputStream input, int length) throws IOException {
       ByteArrayOutputStream output = new ByteArrayOutputStream();
       byte[] buffer = new byte[Math.min(1024, length)];
       int n = input.read(buffer);
       while (n != -1) {
           output.write(buffer, 0, n);
           int remaining = length - output.size();
           if (remaining > 0) {
               n = input.read(buffer, 0, Math.min(buffer.length, remaining));
           } else {
               n = -1;
           }
       }
       return output.toByteArray();
   }


	public int requiredBufferSize() {
		return 0;
	}
			
		
	public static void main(String[] args) {
		
			TikaMIMETypeDetector d = new TikaMIMETypeDetector();
		
	} 
}
