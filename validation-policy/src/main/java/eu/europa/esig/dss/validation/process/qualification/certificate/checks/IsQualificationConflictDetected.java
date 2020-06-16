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
package eu.europa.esig.dss.validation.process.qualification.certificate.checks;

import java.util.Set;

import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationCertificateQualification;
import eu.europa.esig.dss.enumerations.CertificateQualification;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.policy.jaxb.LevelConstraint;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.process.ChainItem;

public class IsQualificationConflictDetected extends ChainItem<XmlValidationCertificateQualification> {

	private final Set<CertificateQualification> certificateQualificationsAtTime;

	public IsQualificationConflictDetected(I18nProvider i18nProvider,
			XmlValidationCertificateQualification result,
			Set<CertificateQualification> certificateQualificationsAtTime, LevelConstraint constraint) {
		super(i18nProvider, result, constraint);
		this.certificateQualificationsAtTime = certificateQualificationsAtTime;
	}

	@Override
	protected boolean process() {
		return Utils.collectionSize(certificateQualificationsAtTime) == 1;
	}

	@Override
	protected MessageTag getAdditionalInfo() {
		if (Utils.collectionSize(certificateQualificationsAtTime) > 1) {
			return MessageTag.RESULTS.setArgs(certificateQualificationsAtTime.toString());
		}
		return null;
	}

	@Override
	protected MessageTag getMessageTag() {
		return MessageTag.QUAL_HAS_CONF;
	}

	@Override
	protected MessageTag getErrorMessageTag() {
		return MessageTag.QUAL_HAS_CONF_ANS;
	}

	@Override
	protected Indication getFailedIndicationForConclusion() {
		return Indication.FAILED;
	}

	@Override
	protected SubIndication getFailedSubIndicationForConclusion() {
		return null;
	}

}
