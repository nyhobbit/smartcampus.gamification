/**
 *    Copyright 2015 Fondazione Bruno Kessler - Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.trentorise.game.model;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.trentorise.game.model.core.GameConcept;
import eu.trentorise.game.repo.GenericObjectPersistence;
import eu.trentorise.game.repo.StatePersistence;

public class PlayerState {

	private final Logger logger = LoggerFactory.getLogger(PlayerState.class);

	private String playerId;
	private String gameId;

	private Set<GameConcept> state = new HashSet<GameConcept>();

	private CustomData customData = new CustomData();

	public PlayerState(String gameId, String playerId) {
		this.playerId = playerId;
		this.gameId = gameId;
	}

	public PlayerState(StatePersistence statePersistence) {
		if (statePersistence != null) {
			ObjectMapper mapper = new ObjectMapper();
			gameId = statePersistence.getGameId();
			playerId = statePersistence.getPlayerId();
			customData = statePersistence.getCustomData();
			state = new HashSet<GameConcept>();
			for (GenericObjectPersistence obj : statePersistence.getConcepts()) {
				try {
					state.add(mapper.convertValue(obj.getObj(),
							(Class<? extends GameConcept>) Thread
									.currentThread().getContextClassLoader()
									.loadClass(obj.getType())));
				} catch (Exception e) {
					logger.error("Problem to load class {}", obj.getType());
				}
			}
		}
	}

	public Set<GameConcept> getState() {
		return state;
	}

	public void setState(Set<GameConcept> state) {
		this.state = state;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public CustomData getCustomData() {
		return customData;
	}

	public void setCustomData(CustomData customData) {
		this.customData = customData;
	}

}