/**
 * 
 */
package uk.co.jemos.protomak.engine.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import uk.co.jemos.protomak.engine.api.ProtoSerialisationService;
import uk.co.jemos.protomak.engine.exceptions.ProtomakEngineSerialisationError;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineConstants;
import uk.co.jemos.xsds.protomak.proto.MessageType;
import uk.co.jemos.xsds.protomak.proto.ProtoType;

/**
 * Implementation of a {@link ProtoSerialisationService} which serialises a
 * {@link ProtoType} to some output directory.
 * 
 * @author mtedone
 * 
 */
public class PojoToProtoSerialisationServiceImpl implements ProtoSerialisationService {

	//------------------->> Constants

	/** The application logger. */
	public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(PojoToProtoSerialisationServiceImpl.class);

	//------------------->> Instance / Static variables

	/** The Singleton instance */
	private static final PojoToProtoSerialisationServiceImpl SINGLETON = new PojoToProtoSerialisationServiceImpl();

	//------------------->> Constructors

	/**
	 * It implements the Singleton pattern.
	 */
	private PojoToProtoSerialisationServiceImpl() {

	}

	//------------------->> Public methods

	/**
	 * This is the Singleton factory method.
	 * 
	 * @return The singleton instance of this class
	 */
	public static PojoToProtoSerialisationServiceImpl getInstance() {
		return SINGLETON;
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeProtoFile(String fileName, File outputPath, ProtoType proto)
			throws ProtomakEngineSerialisationError {

		if (!outputPath.exists()) {
			boolean created = outputPath.mkdirs();
			if (!created) {
				String errMsg = "An error occurred while creating the output folder: " + outputPath;
				LOG.error(errMsg);
				throw new ProtomakEngineSerialisationError(errMsg);

			}
		}

		StringBuilder buff = new StringBuilder();

		List<MessageType> messages = proto.getMessage();
		for (MessageType messageType : messages) {
			buff.append(ProtomakEngineConstants.PROTO_TOKENS_MESSAGE)
					.append(ProtomakEngineConstants.BLANK_SPACE).append(messageType.getName())
					.append(ProtomakEngineConstants.BLANK_SPACE).append("{")
					.append(ProtomakEngineConstants.NEW_LINE).append("}")
					.append(ProtomakEngineConstants.NEW_LINE)
					.append(ProtomakEngineConstants.NEW_LINE);
		}

		if (!fileName.endsWith(".proto")) {
			fileName = fileName + ProtomakEngineConstants.PROTO_SUFFIX;
		}

		BufferedOutputStream bos = null;

		try {
			File outputFileName = new File(outputPath.getAbsolutePath() + File.separatorChar
					+ fileName);
			bos = new BufferedOutputStream(new FileOutputStream(outputFileName));

			bos.write(buff.toString().getBytes("UTF-8"));
			bos.flush();
			LOG.info("The proto content has been serialised to: " + fileName);
		} catch (FileNotFoundException e) {
			String errMsg = "An error occurred while trying to create the output file: " + fileName;
			LOG.error(errMsg, e);
			throw new ProtomakEngineSerialisationError(errMsg, e);

		} catch (UnsupportedEncodingException e) {
			throwGenericSerialisationError(e);
		} catch (IOException e) {
			throwGenericSerialisationError(e);
		} finally {
			IOUtils.closeQuietly(bos);
		}

	}

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	/**
	 * It throws a {@link ProtomakEngineSerialisationError} when an error
	 * occurred during serialisation.
	 * 
	 * @param cause
	 *            The error which occurred.
	 */
	private void throwGenericSerialisationError(Throwable cause) {
		String errMsg = "An error occurred while writing the output buffer to the output file";
		LOG.error(errMsg);
		throw new ProtomakEngineSerialisationError(errMsg, cause);
	}

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}