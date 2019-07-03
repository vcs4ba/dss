/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Set;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.tsp.TimeStampToken;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.SignatureAlgorithm;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.timestamp.TimestampCRLSource;
import eu.europa.esig.dss.validation.timestamp.TimestampOCSPSource;
import eu.europa.esig.dss.validation.timestamp.TimestampToken;
import eu.europa.esig.dss.x509.CertificatePool;
import eu.europa.esig.dss.x509.CertificateSourceType;
import eu.europa.esig.dss.x509.CertificateToken;
import eu.europa.esig.dss.x509.TimestampType;

public class TimestampTokenTest {

	private static final Logger LOG = LoggerFactory.getLogger(TimestampTokenTest.class);

	private static final String TIMETAMPED_DATA_B64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPGFzaWM6QVNpQ0FyY2hpdmVNYW5pZmVzdCB4bWxuczphc2ljPSJodHRwOi8vdXJpLmV0c2kub3JnLzAyOTE4L3YxLjIuMSMiPgoJPGFzaWM6U2lnUmVmZXJlbmNlIFVSST0iTUVUQS1JTkYvYXJjaGl2ZV90aW1lc3RhbXAudHN0IiBNaW1lVHlwZT0iYXBwbGljYXRpb24vdm5kLmV0c2kudGltZXN0YW1wLXRva2VuIi8+Cgk8YXNpYzpEYXRhT2JqZWN0UmVmZXJlbmNlIFVSST0iTUVUQS1JTkYvc2lnbmF0dXJlLnA3cyIgTWltZVR5cGU9ImFwcGxpY2F0aW9uL3gtcGtjczctc2lnbmF0dXJlIj4KCQk8ZHM6RGlnZXN0TWV0aG9kIHhtbG5zOmRzPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIiBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMDQveG1sZW5jI3NoYTI1NiIvPgoJCTxkczpEaWdlc3RWYWx1ZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+M1Flb3M4V01ZWHU1L3E2RzFIdjVnMDVnamtYS2VjSzBVQUxNU2UrZWVJbz08L2RzOkRpZ2VzdFZhbHVlPgoJPC9hc2ljOkRhdGFPYmplY3RSZWZlcmVuY2U+Cgk8YXNpYzpEYXRhT2JqZWN0UmVmZXJlbmNlIFVSST0idG9CZVNpZ25lZC50eHQiIE1pbWVUeXBlPSJ0ZXh0L3BsYWluIj4KCQk8ZHM6RGlnZXN0TWV0aG9kIHhtbG5zOmRzPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIiBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMDQveG1sZW5jI3NoYTI1NiIvPgoJCTxkczpEaWdlc3RWYWx1ZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+SkpadDQxTnQ4VnNZYWhQK1h0aTRyUjN2QkRrVWZSZDZncXVJdGw2UjVPcz08L2RzOkRpZ2VzdFZhbHVlPgoJPC9hc2ljOkRhdGFPYmplY3RSZWZlcmVuY2U+Cgk8YXNpYzpEYXRhT2JqZWN0UmVmZXJlbmNlIFVSST0idG9CZVNpZ25lZC5wZGYiIE1pbWVUeXBlPSJhcHBsaWNhdGlvbi9wZGYiPgoJCTxkczpEaWdlc3RNZXRob2QgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8wNC94bWxlbmMjc2hhMjU2Ii8+CgkJPGRzOkRpZ2VzdFZhbHVlIHhtbG5zOmRzPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIj5JT0lxQ0phWjJXUDF2V0t6VFZsc3Rzeno0RTVod0xhVVBEUnRVVE9YZU5jPTwvZHM6RGlnZXN0VmFsdWU+Cgk8L2FzaWM6RGF0YU9iamVjdFJlZmVyZW5jZT4KCTxhc2ljOkRhdGFPYmplY3RSZWZlcmVuY2UgVVJJPSJNRVRBLUlORi9BU2lDTWFuaWZlc3RfMS54bWwiIE1pbWVUeXBlPSJ0ZXh0L3htbCI+CgkJPGRzOkRpZ2VzdE1ldGhvZCB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyIgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGVuYyNzaGEyNTYiLz4KCQk8ZHM6RGlnZXN0VmFsdWUgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiPmc1ZFloNjFFdkhWdGNCUHMyRG1YZmhYN1lubGxaZzAxMnBid3lkVFR5N2c9PC9kczpEaWdlc3RWYWx1ZT4KCTwvYXNpYzpEYXRhT2JqZWN0UmVmZXJlbmNlPgo8L2FzaWM6QVNpQ0FyY2hpdmVNYW5pZmVzdD4K";

	@Test(expected = CMSException.class)
	public void incorrectTimestamp() throws Exception {
		new TimestampToken(new byte[] { 1, 2, 3 }, TimestampType.ARCHIVE_TIMESTAMP, new CertificatePool());
	}

	@Test
	public void correctToken() throws Exception {
		CertificateToken wrongToken = DSSUtils.loadCertificate(new File("src/test/resources/ec.europa.eu.crt"));

		try (FileInputStream fis = new FileInputStream("src/test/resources/archive_timestamp.tst")) {
			byte[] byteArray = Utils.toByteArray(fis);
			TimestampToken token = new TimestampToken(byteArray, TimestampType.ARCHIVE_TIMESTAMP, new CertificatePool());
			assertNotNull(token);
			LOG.info(token.toString());

			assertFalse(token.isSignedBy(wrongToken));
			assertNotNull(token.getGenerationTime());
			assertNotNull(token.getAbbreviation());
			assertTrue(Utils.isCollectionNotEmpty(token.getCertificates()));
			assertEquals(TimestampType.ARCHIVE_TIMESTAMP, token.getTimeStampType());
			assertEquals(DigestAlgorithm.SHA256, token.getSignedDataDigestAlgo());
			assertTrue(Utils.isArrayNotEmpty(token.getMessageImprintDigest()));
			assertNull(token.getSignatureAlgorithm());

			List<CertificateToken> tstCerts = token.getCertificates();
			for (CertificateToken certificateToken : tstCerts) {
				if (token.isSignedBy(certificateToken)) {
					break;
				}
			}

			assertNotNull(token.getPublicKeyOfTheSigner());

			assertNotNull(token.getSignatureAlgorithm());
			assertEquals(SignatureAlgorithm.RSA_SHA256, token.getSignatureAlgorithm());
			assertFalse(token.isSelfSigned());

			assertFalse(token.matchData(null));

			assertFalse(token.matchData(new byte[] { 1, 2, 3 }));
			assertTrue(token.isMessageImprintDataFound());
			assertFalse(token.isMessageImprintDataIntact());

			assertTrue(token.matchData(Utils.fromBase64(TIMETAMPED_DATA_B64)));
			assertTrue(token.isMessageImprintDataFound());
			assertTrue(token.isMessageImprintDataIntact());

			byte[] encoded = token.getEncoded();
			TimeStampToken tst = new TimeStampToken(new CMSSignedData(encoded));
			assertNotNull(tst);
		}
	}

	@Test
	public void eeTSTwithCerts() throws Exception {
		// request has been done with certReq = true
		// the token includes the certificate chain
		String base64TST = "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";
		TimestampToken timestampToken = new TimestampToken(Utils.fromBase64(base64TST), TimestampType.SIGNATURE_TIMESTAMP, new CertificatePool());
		assertNotNull(timestampToken);
		List<CertificateToken> certificates = timestampToken.getCertificates();
		assertTrue(Utils.isCollectionNotEmpty(certificates));
		for (CertificateToken certificateToken : certificates) {
			if (timestampToken.isSignedBy(certificateToken)) {
				break;
			}
		}
		assertTrue(timestampToken.isSignatureValid());
		assertTrue(timestampToken.matchData("Hello world".getBytes()));
	}

	@Test
	public void eeTSTwithoutCerts() throws Exception {
		// request has been done with certReq = false
		String base64TST = "MIAGCSqGSIb3DQEHAqCAMIIDvgIBAzEPMA0GCWCGSAFlAwQCAwUAMIGMBgsqhkiG9w0BCRABBKB9BHsweQIBAQYGBACPZwEBMFEwDQYJYIZIAWUDBAIDBQAEQLf3g7rtgpfw25F0YhhP9PCOacLV5fealCYA+XJfWM4fKcGBOb+AsGwP/yvdNHOEUuz0DEiMIqfj2Azfb5wcDUcCCBa/WWL5098BGA8yMDE4MDgwMTE0MzEzOVoxggMXMIIDEwIBATCBkTB9MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEwMC4GA1UEAwwnVEVTVCBvZiBFRSBDZXJ0aWZpY2F0aW9uIENlbnRyZSBSb290IENBMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUCEE6s+2wj/FuOVAWWu7c7U0wwDQYJYIZIAWUDBAIDBQCgggFWMBoGCSqGSIb3DQEJAzENBgsqhkiG9w0BCRABBDAcBgkqhkiG9w0BCQUxDxcNMTgwODAxMTQzMTM5WjBPBgkqhkiG9w0BCQQxQgRAugviPUUMFKSe765lT1N6vY8xfngIhElj+q8qLwSn+T3sDHy7mGH9HgWg4ymttofnaO2AcbvLg+Crx+O+3ro5qjCByAYLKoZIhvcNAQkQAgwxgbgwgbUwgbIwga8EFAKxl+94ruFx9qFHX1DqzGVx8fwLMIGWMIGBpH8wfTELMAkGA1UEBhMCRUUxIjAgBgNVBAoMGUFTIFNlcnRpZml0c2VlcmltaXNrZXNrdXMxMDAuBgNVBAMMJ1RFU1Qgb2YgRUUgQ2VydGlmaWNhdGlvbiBDZW50cmUgUm9vdCBDQTEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVlAhBOrPtsI/xbjlQFlru3O1NMMA0GCSqGSIb3DQEBAQUABIIBABNzVCdb7st5hDZAJTECbaFm1NAvt+r7fcJVjb+XJErb/yT3wBbouwrs1B6AhlMlr39ivzKltP6kT9yHpCWySzi66c++V1yGZEXsoH7tAZcEBTEsye+JVN5D71OoRhY9CAacZYxxoMcpa8/t/2aFFNoBOYbKlqUXklqAtEumjxvfK4yYzGcU7ESTNumMOMkg6bt1mAnaDvxtzamyjDzwUTKOr0R8s66y+zGXYXeJywX+hNIFpbme1RRbcxKs5src32J1JCLgL1gDuTMOwJKgCH+BtqRduK6KHAgwR0TWMhYyZPauesjJZ/o8dJgzUwmapl3Y++aF6UzpfC2uXboJuQkAAAAA";
		TimestampToken timestampToken = new TimestampToken(Utils.fromBase64(base64TST), TimestampType.SIGNATURE_TIMESTAMP, new CertificatePool());
		assertNotNull(timestampToken);
		assertFalse(Utils.isCollectionNotEmpty(timestampToken.getCertificates()));
		assertFalse(timestampToken.isSignatureValid());
		assertTrue(timestampToken.matchData("Hello world".getBytes()));
	}

	@Test
	public void eeTSTwithoutCertsAndCertPool() throws Exception {
		// request has been done with certReq = false
		String base64TST = "MIAGCSqGSIb3DQEHAqCAMIIDvgIBAzEPMA0GCWCGSAFlAwQCAwUAMIGMBgsqhkiG9w0BCRABBKB9BHsweQIBAQYGBACPZwEBMFEwDQYJYIZIAWUDBAIDBQAEQLf3g7rtgpfw25F0YhhP9PCOacLV5fealCYA+XJfWM4fKcGBOb+AsGwP/yvdNHOEUuz0DEiMIqfj2Azfb5wcDUcCCBa/WWL5098BGA8yMDE4MDgwMTE0MzEzOVoxggMXMIIDEwIBATCBkTB9MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEwMC4GA1UEAwwnVEVTVCBvZiBFRSBDZXJ0aWZpY2F0aW9uIENlbnRyZSBSb290IENBMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUCEE6s+2wj/FuOVAWWu7c7U0wwDQYJYIZIAWUDBAIDBQCgggFWMBoGCSqGSIb3DQEJAzENBgsqhkiG9w0BCRABBDAcBgkqhkiG9w0BCQUxDxcNMTgwODAxMTQzMTM5WjBPBgkqhkiG9w0BCQQxQgRAugviPUUMFKSe765lT1N6vY8xfngIhElj+q8qLwSn+T3sDHy7mGH9HgWg4ymttofnaO2AcbvLg+Crx+O+3ro5qjCByAYLKoZIhvcNAQkQAgwxgbgwgbUwgbIwga8EFAKxl+94ruFx9qFHX1DqzGVx8fwLMIGWMIGBpH8wfTELMAkGA1UEBhMCRUUxIjAgBgNVBAoMGUFTIFNlcnRpZml0c2VlcmltaXNrZXNrdXMxMDAuBgNVBAMMJ1RFU1Qgb2YgRUUgQ2VydGlmaWNhdGlvbiBDZW50cmUgUm9vdCBDQTEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVlAhBOrPtsI/xbjlQFlru3O1NMMA0GCSqGSIb3DQEBAQUABIIBABNzVCdb7st5hDZAJTECbaFm1NAvt+r7fcJVjb+XJErb/yT3wBbouwrs1B6AhlMlr39ivzKltP6kT9yHpCWySzi66c++V1yGZEXsoH7tAZcEBTEsye+JVN5D71OoRhY9CAacZYxxoMcpa8/t/2aFFNoBOYbKlqUXklqAtEumjxvfK4yYzGcU7ESTNumMOMkg6bt1mAnaDvxtzamyjDzwUTKOr0R8s66y+zGXYXeJywX+hNIFpbme1RRbcxKs5src32J1JCLgL1gDuTMOwJKgCH+BtqRduK6KHAgwR0TWMhYyZPauesjJZ/o8dJgzUwmapl3Y++aF6UzpfC2uXboJuQkAAAAA";
		TimestampToken timestampToken = new TimestampToken(Utils.fromBase64(base64TST), TimestampType.SIGNATURE_TIMESTAMP, new CertificatePool());
		assertNotNull(timestampToken);
		assertTrue(Utils.isCollectionEmpty(timestampToken.getCertificates()));
		assertFalse(timestampToken.isSignatureValid());
		assertTrue(timestampToken.matchData("Hello world".getBytes()));

		CertificatePool certPool = new CertificatePool();
		String base64TsuCert = "MIIEFTCCAv2gAwIBAgIQTqz7bCP8W45UBZa7tztTTDANBgkqhkiG9w0BAQsFADB9MQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEwMC4GA1UEAwwnVEVTVCBvZiBFRSBDZXJ0aWZpY2F0aW9uIENlbnRyZSBSb290IENBMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUwHhcNMTQwOTAyMTAwNjUxWhcNMjQwOTAyMTAwNjUxWjBdMQswCQYDVQQGEwJFRTEiMCAGA1UECgwZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEMMAoGA1UECwwDVFNBMRwwGgYDVQQDDBNERU1PIG9mIFNLIFRTQSAyMDE0MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAysgrVnVPxH8jNgCsJw0y+7fmmBDTM/tNB+xielnP9KcuQ+nyTgNu1JMpnry7Rh4ndr54rPLXNGVdb/vsgsi8B558DisPVUn3Rur3/8XQ+BCkhTQIg1cSmyCsWxJgeaQKJi6WGVaQWB2he35aVhL5F6ae/gzXT3sGGwnWujZkY9o5RapGV15+/b7Uv+7jWYFAxcD6ba5jI00RY/gmsWwKb226Rnz/pXKDBfuN3ox7y5/lZf5+MyIcVe1qJe7VAJGpJFjNq+BEEdvfqvJ1PiGQEDJAPhRqahVjBSzqZhJQoL3HI42NRCFwarvdnZYoCPxjeYpAynTHgNR7kKGX1iQ8OQIDAQABo4GwMIGtMA4GA1UdDwEB/wQEAwIGwDAWBgNVHSUBAf8EDDAKBggrBgEFBQcDCDAdBgNVHQ4EFgQUJwScZQxzlzySVqZXviXpKZDV5NwwHwYDVR0jBBgwFoAUtTQKnaUvEMXnIQ6+xLFlRxsDdv4wQwYDVR0fBDwwOjA4oDagNIYyaHR0cHM6Ly93d3cuc2suZWUvcmVwb3NpdG9yeS9jcmxzL3Rlc3RfZWVjY3JjYS5jcmwwDQYJKoZIhvcNAQELBQADggEBAIq02SVKwP1UolKjqAQe7SVY/Kgi++G2kqAd40UmMqa94GTu91LFZR5TvdoyZjjnQ2ioXh5CV2lflUy/lUrZMDpqEe7IbjZW5+b9n5aBvXYJgDua9SYjMOrcy3siytqq8UbNgh79ubYgWhHhJSnLWK5YJ+5vQjTpOMdRsLp/D+FhTUa6mP0UDY+U82/tFufkd9HW4zbalUWhQgnNYI3oo0CsZ0HExuynOOZmM1Bf8PzD6etlLSKkYB+mB77Omqgflzz+Jjyh45o+305MRzHDFeJZx7WxC+XTNWQ0ZFTFfc0ozxxzUWUlfNfpWyQh3+4LbeSQRWrNkbNRfCpYotyM6AY=";
		CertificateToken tsuCert = DSSUtils.loadCertificateFromBase64EncodedString(base64TsuCert);
		certPool.getInstance(tsuCert, CertificateSourceType.OTHER);

		SignatureValidationContext svc = new SignatureValidationContext(certPool);
		svc.initialize(new CommonCertificateVerifier());
		svc.addTimestampTokenForVerification(timestampToken);
		svc.validate();

		Set<TimestampToken> timestamps = svc.getProcessedTimestamps();
		assertEquals(1, timestamps.size());
		TimestampToken validationResult = timestamps.iterator().next();
		assertTrue(validationResult.isSignatureValid());
	}
	
	@Test
	public void cadesSignatureTimestampTest() throws Exception {
		String base64TST = "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";
		TimestampToken timestampToken = new TimestampToken(Utils.fromBase64(base64TST), TimestampType.SIGNATURE_TIMESTAMP);
		assertNotNull(timestampToken);

		assertEquals(2, timestampToken.getCertificates().size());
		assertEquals(2, timestampToken.getCertificateRefs().size());
		
		TimestampCRLSource crlSource = timestampToken.getCRLSource();
		assertNotNull(crlSource);
		assertEquals(1, crlSource.getAllCRLIdentifiers().size());
		assertEquals(1, crlSource.getAllCRLReferences().size());
	}
	
	@Test
	public void cadesArchiveTimestampV2Test() throws Exception {
		String base64TST = "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";
		TimestampToken timestampToken = new TimestampToken(Utils.fromBase64(base64TST), TimestampType.ARCHIVE_TIMESTAMP);
		assertNotNull(timestampToken);

		assertEquals(2, timestampToken.getCertificates().size());
		assertEquals(1, timestampToken.getCertificateRefs().size());

		TimestampCRLSource crlSource = timestampToken.getCRLSource();
		assertNotNull(crlSource);
		assertEquals(1, crlSource.getAllCRLIdentifiers().size());
		assertEquals(0, crlSource.getAllCRLReferences().size());
		
		TimestampOCSPSource ocspSource = timestampToken.getOCSPSource();
		assertNotNull(ocspSource);
		assertEquals(0, ocspSource.getAllOCSPIdentifiers().size());
		assertEquals(0, ocspSource.getAllOCSPReferences().size());
	}

	@Test
	public void timestampSigCert() throws Exception {
		String base64 = "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";
		TimestampToken timestampToken = new TimestampToken(Utils.fromBase64(base64), TimestampType.SIGNATURE_TIMESTAMP);
		assertNotNull(timestampToken);

		assertEquals(1, timestampToken.getCertificates().size());
		assertEquals(2, timestampToken.getCertificateRefs().size());

		TimestampCRLSource crlSource = timestampToken.getCRLSource();
		assertNotNull(crlSource);
		assertEquals(0, crlSource.getAllCRLIdentifiers().size());
		assertEquals(0, crlSource.getAllCRLReferences().size());

		TimestampOCSPSource ocspSource = timestampToken.getOCSPSource();
		assertNotNull(ocspSource);
		assertEquals(0, ocspSource.getAllOCSPIdentifiers().size());
		assertEquals(0, ocspSource.getAllOCSPReferences().size());
	}

}
