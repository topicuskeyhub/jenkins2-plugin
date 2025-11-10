package nl.topicus.keyhub.jenkins.vault;

import java.time.Instant;
import java.util.List;

import com.topicus.keyhub.sdk.models.vault.VaultRecord;

public class EmptyVaultAccessor implements IVaultAccessor {
	private final Instant expires = Instant.now().plusSeconds(10);

	@Override
	public boolean isExpired() {
		return expires.isBefore(Instant.now());
	}

	@Override
	public List<VaultRecord> fetchRecordsFromVault() {
		return List.of();
	}

	@Override
	public VaultRecord fetchRecordSecret(String href) {
		return null;
	}
}
