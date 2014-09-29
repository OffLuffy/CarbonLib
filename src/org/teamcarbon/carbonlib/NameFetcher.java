package org.teamcarbon.carbonlib;

import com.google.common.collect.ImmutableList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

@SuppressWarnings("UnusedDeclaration")
public final class NameFetcher implements Callable<Map<UUID, String>> {
	private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
	private final JSONParser jsonParser = new JSONParser();
	private final List<UUID> uuids;
	public NameFetcher(List<UUID> uuids) {
		this.uuids = ImmutableList.copyOf(uuids);
	}
	public NameFetcher(UUID uuid) { uuids = new ArrayList<UUID>(); uuids.add(uuid); }
	@Override
	public Map<UUID, String> call() throws Exception {
		Map<UUID, String> uuidStringMap = new HashMap<UUID, String>();
		for (UUID uuid: uuids) {
			HttpURLConnection connection = (HttpURLConnection) new URL(PROFILE_URL+uuid.toString().replace("-", "")).openConnection();
			JSONObject response = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
			String name = (String) response.get("name");
			if (name == null)
				continue;
			String cause = (String) response.get("cause");
			String errorMessage = (String) response.get("errorMessage");
			if (cause != null && cause.length() > 0)
				throw new IllegalStateException(errorMessage);
			uuidStringMap.put(uuid, name);
		}
		return uuidStringMap;
	}
	/**
	 * Returns the current name of the player that matches the provided UUID
	 * @param uuid The UUID of the player's Mojang account
	 * @return The player's current character name associated with the UUID
	 * @throws Exception
	 */
	public static String getNameOf(UUID uuid) throws Exception { return new NameFetcher(Arrays.asList(uuid)).call().get(uuid); }
}
