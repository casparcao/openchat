package top.mikecao.openchat.client.model;

import lombok.Data;
import lombok.experimental.Accessors;
import mjson.Json;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mike
 */
public class EmojiOne {

	private static final HashMap<String, EmojiEntry> EMOJI_ENTRY_HASH_MAP = new HashMap<>();

	private static final EmojiOne INSTANCE = new EmojiOne();

	public static EmojiOne getInstance() {
		return INSTANCE;
	}


	private static final String EMOJIONE_JSON_FILE = "/emoji/emoji.json";
	private static final String EMOJIONE_KEY_NAME = "name";
	private static final String EMOJIONE_KEY_SHORTNAME = "shortname";
	private static final String EMOJIONE_KEY_UNICODE_ALT = "unicode_alt";
	private static final String EMOJIONE_KEY_UNICODE = "unicode";
	private static final String EMOJIONE_KEY_ALIASES = "aliases";
	private static final String EMOJIONE_KEY_ALIASES_ASCII = "aliases_ascii";
	private static final String EMOJIONE_KEY_KEYWORDS = "keywords";
	private static final String EMOJIONE_KEY_CATEGORY = "category";
	private static final String EMOJIONE_KEY_EMOJI_ORDER = "emoji_order";

	private static final String EMOJIONE_MODIFIER = "modifier";


	private EmojiOne() {
		URL url = EmojiOne.class.getResource(EMOJIONE_JSON_FILE);
		assert url != null;
		Json j = Json.read(url);
		Map<String, Json> map = j.asJsonMap();
		map.forEach((key, value) -> {
			Map<String, Json> valueMap = value.asJsonMap();
			String name = valueMap.get(EMOJIONE_KEY_NAME).asString();
			String shortname = valueMap.get(EMOJIONE_KEY_SHORTNAME).asString();
			List<String> unicodes = valueMap.get(EMOJIONE_KEY_UNICODE_ALT).asList().stream().map(String.class::cast).collect(Collectors.toList());
			unicodes.add(valueMap.get(EMOJIONE_KEY_UNICODE).asString());
			List<String> aliases = valueMap.get(EMOJIONE_KEY_ALIASES).asList().stream().map(String.class::cast).collect(Collectors.toList());
			List<String> aliasesAscii = valueMap.get(EMOJIONE_KEY_ALIASES_ASCII).asList().stream().map(String.class::cast).collect(Collectors.toList());
			List<String> keywords = valueMap.get(EMOJIONE_KEY_KEYWORDS).asList().stream().map(String.class::cast).distinct().collect(Collectors.toList());
			String category = valueMap.get(EMOJIONE_KEY_CATEGORY).asString();
			int emojiOrder = valueMap.get(EMOJIONE_KEY_EMOJI_ORDER).asInteger();

			EmojiEntry entry = new EmojiEntry();
			entry.setName(name);
			entry.setShortname(shortname);
			entry.setUnicodes(unicodes);
			entry.setAliases(aliases);
			entry.setAliasesAscii(aliasesAscii);
			entry.setKeywords(keywords);
			entry.setCategory(category);
			entry.setEmojiOrder(emojiOrder);
			EmojiOne.EMOJI_ENTRY_HASH_MAP.put(shortname, entry);
		});
	}

	public List<String> getCategories() {
		return EMOJI_ENTRY_HASH_MAP.values().stream().map(EmojiEntry::getCategory).distinct().collect(Collectors.toList());
	}

	public Map<String, List<Emoji>> getCategorizedEmojis(int tone) {
		Map<String, List<Emoji>> map = new HashMap<>();
		getTonedEmojis(tone).forEach(emojiEntry -> {
			if (emojiEntry.getCategory().equals(EMOJIONE_MODIFIER)) {
				return;
			}
			for (int i = 1; i <= 6; i++) {
				if (i == tone) {
					continue;
				}
				if (emojiEntry.getShortname().endsWith("_tone" + i + ":")) {
					return;
				}
			}
			List<Emoji> list = map.computeIfAbsent(emojiEntry.getCategory(), k -> new ArrayList<>());
			Emoji emoji = new Emoji(emojiEntry.getShortname(), convert(emojiEntry.getLastUnicode()),
					emojiEntry.getLastUnicode());
			emoji.setEmojiOrder(emojiEntry.getEmojiOrder());
			list.add(emoji);
		});

		map.values().forEach(list->list.sort(Comparator.comparing(Emoji::getEmojiOrder)));

		return map;
	}

	public List<EmojiEntry> getTonedEmojis(int tone) {
		List<EmojiEntry> allToned = new ArrayList<>();
		List<EmojiEntry> selectedTone = new ArrayList<>();
		List<EmojiEntry> defaultTone = new ArrayList<>();
		EMOJI_ENTRY_HASH_MAP.values().forEach(emojiEntry -> {
			for(int i = 1; i <= 5; i++) {
				if(emojiEntry.getShortname().endsWith("_tone" +i+":")) {
					allToned.add(emojiEntry);
					if(emojiEntry.getShortname().endsWith(tone + ":")) {
						selectedTone.add(emojiEntry);
					}
					String withoutTone = emojiEntry.getShortname().substring(0,emojiEntry.getShortname().length()-7) + ":";
					EmojiEntry emojiEntryWithoutTone = EMOJI_ENTRY_HASH_MAP.get(withoutTone);
					if(!defaultTone.contains(emojiEntryWithoutTone)) {
						defaultTone.add(emojiEntryWithoutTone);
					}
				}
			}
		});
		List<EmojiEntry> allEmojis = new ArrayList<>(EMOJI_ENTRY_HASH_MAP.values());
		allEmojis.removeAll(allToned);
		allEmojis.removeAll(defaultTone);
		if(tone == 6) {
			allEmojis.addAll(defaultTone);
		} else {
			allEmojis.addAll(selectedTone);
		}
		return allEmojis;

	}

	private String convert(String unicodeStr) {
		if (unicodeStr.isEmpty()) {
			return unicodeStr;
		}
		String[] parts = unicodeStr.split("-");
		StringBuilder buff = new StringBuilder();
		for (String s : parts) {
			int part = Integer.parseInt(s, 16);
			if (part >= 0x10000 && part <= 0x10FFFF) {
				int hi = (int) (Math.floor((part - 0x10000) / 0x400) + 0xD800);
				int lo = ((part - 0x10000) % 0x400) + 0xDC00;
				buff.append(new String(Character.toChars(hi))).append(new String(Character.toChars(lo)));
			} else {
				buff.append(new String(Character.toChars(part)));
			}
		}
		return buff.toString();
	}

	public List<Emoji> search(String text) {
		return EMOJI_ENTRY_HASH_MAP.values().stream().filter(emojiEntry -> (emojiEntry.getShortname().contains(text)
		|| emojiEntry.getAliases().contains(text) || emojiEntry.getAliasesAscii().contains(text))
		|| emojiEntry.getName().contains(text)).map(emojiEntry ->
			new Emoji(emojiEntry.getShortname(), convert(emojiEntry.getLastUnicode()), emojiEntry.getLastUnicode())).collect(Collectors.toList());
	}

	public Emoji getEmoji(String shortname) {
		EmojiEntry entry = EMOJI_ENTRY_HASH_MAP.get(shortname);
		if(entry == null) {
			return null;
		}
		return new Emoji(entry.getShortname(), convert(entry.getLastUnicode()), entry.getLastUnicode());
	}

	@Data
	@Accessors(chain = true)
	static class EmojiEntry {
		private String name;
		private String shortname;
		private List<String> unicodes;
		private List<String> aliases;
		private List<String> aliasesAscii;
		private List<String> keywords;
		private String category;
		private int emojiOrder;

		public EmojiEntry() {
			//...
		}

		public String getLastUnicode() {
			if (unicodes.isEmpty()) {
				return null;
			}
			return unicodes.get(unicodes.size() - 1);
		}

	}

}
