package io.github.wasabithumb.xclaim.i18n;

import static io.github.wasabithumb.xclaim.i18n.Translatable.keyed;

@SuppressWarnings("unused")
public final class I18N {

    /** Done */
    public static final Translatable STARTUP_DONE = keyed("startup-done");

    /** Done */
    public static final Translatable DISABLE_DONE = keyed("disable-done");

    /** New Claim */
    public static final Translatable NEW_CLAIM = keyed("new-claim");

    /** Created data folder */
    public static final Translatable DATA_FOLDER_CREATED = keyed("data-folder-created");

    /** Locating JAR file */
    public static final Translatable LOCATING_JAR = keyed("locating-jar");

    /** Fetching required libraries... */
    public static final Translatable EXT_DL = keyed("ext-dl");

    /** (!) An update for XClaim is available ($1)! */
    public static final Translatable UPDATE_AVAILABLE_LINE1 = keyed("update-available-line1");

    /** (!) Use /xc update to update automatically. */
    public static final Translatable UPDATE_AVAILABLE_LINE2 = keyed("update-available-line2");

    /** Checking for Dynmap */
    public static final Translatable DYNMAP_CHECK = keyed("dynmap-check");

    /** Failed to validate Dynmap core, proceeding anyways... */
    public static final Translatable DYNMAP_WARN_CORE = keyed("dynmap-warn-core");

    /** Hooked into Dynmap version $1 */
    public static final Translatable DYNMAP_HOOKED = keyed("dynmap-hooked");

    /** Claims */
    public static final Translatable DYNMAP_MARKER_NAME = keyed("dynmap-marker-name");

    /** No suitable economy plugin was found */
    public static final Translatable ECO_FAIL = keyed("eco-fail");

    /** Loading trusted players */
    public static final Translatable TRUST_LOAD = keyed("trust-load");

    /** An error occurred while loading trusted players. See details below. */
    public static final Translatable TRUST_LOAD_ERR = keyed("trust-load-err");

    /** (!) XClaim now uses config.toml! A file has been generated, but will not be used unless config.yml is removed. */
    public static final Translatable CONFIG_MIGRATION_LINE1 = keyed("config-migration-line1");

    /** (!) Support for YAML configs may be dropped in the future. */
    public static final Translatable CONFIG_MIGRATION_LINE2 = keyed("config-migration-line2");

    /** Loading claims */
    public static final Translatable CLAIMS_LOAD = keyed("claims-load");

    /** An error occurred while loading claims. See details below. */
    public static final Translatable CLAIMS_LOAD_ERR = keyed("claims-load-err");

    /** Unpacking claims */
    public static final Translatable CLAIMS_UNPACK = keyed("claims-unpack");

    /** Claim "$1" is corrupt (Reason: $2). Skipping... */
    public static final Translatable CLAIMS_UNPACK_ERR = keyed("claims-unpack-err");

    /** Not a section */
    public static final Translatable CLAIMS_UNPACK_ERR_SECTION = keyed("claims-unpack-err-section");

    /** Initializing chunk editor */
    public static final Translatable SERVICES_CHUNK_EDITOR = keyed("services-chunk-editor");

    /** Loading command manager */
    public static final Translatable SERVICES_COMMAND = keyed("services-command");

    /** Registering default commands */
    public static final Translatable SERVICES_COMMAND_REGISTER = keyed("services-command-register");

    /** Starting movement routine */
    public static final Translatable SERVICES_MOVEMENT = keyed("services-movement");

    /** Refreshing grace routine */
    public static final Translatable SERVICES_GRACE = keyed("services-grace");

    /** Saving trusted players */
    public static final Translatable TRUST_SAVE = keyed("trust-save");

    /** Created new trust.yml */
    public static final Translatable TRUST_SAVE_NEW = keyed("trust-save-new");

    /** An error occurred while saving trusted players. See details below. */
    public static final Translatable TRUST_SAVE_ERR = keyed("trust-save-err");

    /** Saving claims */
    public static final Translatable CLAIMS_SAVE = keyed("claims-save");

    /** Created new claims.yml */
    public static final Translatable CLAIMS_SAVE_NEW = keyed("claims-save-new");

    /** An error occurred while saving claims. See details below. */
    public static final Translatable CLAIMS_SAVE_ERR = keyed("claims-save-err");

    /** Stopping services */
    public static final Translatable SERVICES_STOP = keyed("services-stop");

    /** * This chunk is already taken! */
    public static final Translatable CHUNK_EDITOR_TAKEN = keyed("chunk-editor-taken");

    /** * You can't add chunks from this world to this claim! */
    public static final Translatable CHUNK_EDITOR_WRONG_WORLD = keyed("chunk-editor-wrong-world");

    /** * A region protection plugin is blocking you from claiming this chunk! */
    public static final Translatable CHUNK_EDITOR_PROTECTION_DENY = keyed("chunk-editor-protection-deny");

    /** * Cannot claim this close to another claim you do not have access to! */
    public static final Translatable CHUNK_EDITOR_MIN_DISTANCE_DENY = keyed("chunk-editor-min-distance-deny");

    /** * Chunks in your claim must be next to each other! */
    public static final Translatable CHUNK_EDITOR_ADJACENT = keyed("chunk-editor-adjacent");

    /** * You've reached your maximum number of chunks. Try deleting some. */
    public static final Translatable CHUNK_EDITOR_MAX = keyed("chunk-editor-max");

    /** * Claimed chunk at X=$1, Z=$2 */
    public static final Translatable CHUNK_EDITOR_ADD = keyed("chunk-editor-add");

    /** * This claim already contains this chunk. */
    public static final Translatable CHUNK_EDITOR_REDUNDANT_ADD = keyed("chunk-editor-redundant-add");

    /** Unclaimed chunk! */
    public static final Translatable CHUNK_EDITOR_REMOVE = keyed("chunk-editor-remove");

    /** * This chunk was already not part of this claim. */
    public static final Translatable CHUNK_EDITOR_REDUNDANT_REMOVE = keyed("chunk-editor-redundant-remove");

    /** = Chunk at $1, $2 = */
    public static final Translatable CHUNK_EDITOR_INFO = keyed("chunk-editor-info");

    /** Open */
    public static final Translatable CHUNK_EDITOR_INFO_OPEN = keyed("chunk-editor-info-open");

    /** Claimed */
    public static final Translatable CHUNK_EDITOR_INFO_CLAIMED = keyed("chunk-editor-info-claimed");

    /** In a different claim you own */
    public static final Translatable CHUNK_EDITOR_INFO_OWNED = keyed("chunk-editor-info-owned");

    /** Taken by $1 */
    public static final Translatable CHUNK_EDITOR_INFO_TAKEN = keyed("chunk-editor-info-taken");

    /** Claim */
    public static final Translatable CHUNK_EDITOR_CLAIM = keyed("chunk-editor-claim");

    /** Unclaim */
    public static final Translatable CHUNK_EDITOR_UNCLAIM = keyed("chunk-editor-unclaim");

    /** Quit */
    public static final Translatable CHUNK_EDITOR_QUIT = keyed("chunk-editor-quit");

    /** * You need $1 in order to purchase this chunk! */
    public static final Translatable CHUNK_EDITOR_CANT_AFFORD = keyed("chunk-editor-cant-afford");

    /** * Failed to deduct $1 from your account. */
    public static final Translatable CHUNK_EDITOR_PAY_FAIL = keyed("chunk-editor-pay-fail");

    /** * Paid $1 for this chunk */
    public static final Translatable CHUNK_EDITOR_PAY_SUCCESS = keyed("chunk-editor-pay-success");

    /** * Rewarded $1 for unclaiming this chunk */
    public static final Translatable CHUNK_EDITOR_REWARD = keyed("chunk-editor-reward");

    /** Hey! You can't do that here. */
    public static final Translatable PERM_HANDLER_STD_ERROR = keyed("permHandler-stdError");

    /** Build */
    public static final Translatable PERM_BUILD_NAME = keyed("perm-build-name");

    /** Place blocks */
    public static final Translatable PERM_BUILD_DESCRIPTION = keyed("perm-build-description");

    /** Break */
    public static final Translatable PERM_BREAK_NAME = keyed("perm-break-name");

    /** Break blocks */
    public static final Translatable PERM_BREAK_DESCRIPTION = keyed("perm-break-description");

    /** Enter */
    public static final Translatable PERM_ENTER_NAME = keyed("perm-enter-name");

    /** Walk into the area */
    public static final Translatable PERM_ENTER_DESCRIPTION = keyed("perm-enter-description");

    /** Interact */
    public static final Translatable PERM_INTERACT_NAME = keyed("perm-interact-name");

    /** Interact with buttons, chests, etc. */
    public static final Translatable PERM_INTERACT_DESCRIPTION = keyed("perm-interact-description");

    /** Open Chests */
    public static final Translatable PERM_CHEST_OPEN_NAME = keyed("perm-chest-open-name");

    /** Interact with chests, for general interaction see Interact. */
    public static final Translatable PERM_CHEST_OPEN_DESCRIPTION = keyed("perm-chest-open-description");

    /** Place Entities */
    public static final Translatable PERM_ENT_PLACE_NAME = keyed("perm-ent-place-name");

    /** Put down boats, minecarts, armor stands, etc. */
    public static final Translatable PERM_ENT_PLACE_DESCRIPTION = keyed("perm-ent-place-description");

    /** Place Vehicles */
    public static final Translatable PERM_VEHICLE_PLACE_NAME = keyed("perm-vehicle-place-name");

    /** Put down vehicles like minecarts */
    public static final Translatable PERM_VEHICLE_PLACE_DESCRIPTION = keyed("perm-vehicle-place-description");

    /** Use Flammables */
    public static final Translatable PERM_FIRE_USE_NAME = keyed("perm-fire-use-name");

    /** Use flint & steel and fire charges */
    public static final Translatable PERM_FIRE_USE_DESCRIPTION = keyed("perm-fire-use-description");

    /** Damage Friendly Entities */
    public static final Translatable PERM_ENTITY_DAMAGE_FRIENDLY_NAME = keyed("perm-entity-damage-friendly-name");

    /** Cause damage to things like cows, sheep, squid, etc */
    public static final Translatable PERM_ENTITY_DAMAGE_FRIENDLY_DESCRIPTION = keyed("perm-entity-damage-friendly-description");

    /** Damage Hostile Entities */
    public static final Translatable PERM_ENTITY_DAMAGE_HOSTILE_NAME = keyed("perm-entity-damage-hostile-name");

    /** Cause damage to things like zombies, skeletons, slimes, etc */
    public static final Translatable PERM_ENTITY_DAMAGE_HOSTILE_DESCRIPTION = keyed("perm-entity-damage-hostile-description");

    /** Damage Vehicles Entities */
    public static final Translatable PERM_ENTITY_DAMAGE_VEHICLE_NAME = keyed("perm-entity-damage-vehicle-name");

    /** Cause damage to things like boats and minecarts */
    public static final Translatable PERM_ENTITY_DAMAGE_VEHICLE_DESCRIPTION = keyed("perm-entity-damage-vehicle-description");

    /** Damage Non-Living Entities */
    public static final Translatable PERM_ENTITY_DAMAGE_NL_NAME = keyed("perm-entity-damage-nl-name");

    /** Cause damage to things like armor stands and decorations */
    public static final Translatable PERM_ENTITY_DAMAGE_NL_DESCRIPTION = keyed("perm-entity-damage-nl-description");

    /** Damage Miscellaneous Entities */
    public static final Translatable PERM_ENTITY_DAMAGE_MISC_NAME = keyed("perm-entity-damage-misc-name");

    /** Cause damage to entities that don't fall into any other group */
    public static final Translatable PERM_ENTITY_DAMAGE_MISC_DESCRIPTION = keyed("perm-entity-damage-misc-description");

    /** Explosions */
    public static final Translatable PERM_EXPLODE_NAME = keyed("perm-explode-name");

    /** Cause explosions; when the source of an explosion is unclear, TRUSTED and VETERAN groups act like NONE */
    public static final Translatable PERM_EXPLODE_DESCRIPTION = keyed("perm-explode-description");

    /** Drop Items */
    public static final Translatable PERM_ITEM_DROP_NAME = keyed("perm-item-drop-name");

    /** Drop items */
    public static final Translatable PERM_ITEM_DROP_DESCRIPTION = keyed("perm-item-drop-description");

    /** Manage Claim */
    public static final Translatable PERM_MANAGE_NAME = keyed("perm-manage-name");

    /** Modify the claim settings */
    public static final Translatable PERM_MANAGE_DESCRIPTION = keyed("perm-manage-description");

    /** Remove Claim */
    public static final Translatable PERM_DELETE_NAME = keyed("perm-delete-name");

    /** Remove the claim */
    public static final Translatable PERM_DELETE_DESCRIPTION = keyed("perm-delete-description");

    /** XClaim Config */
    public static final Translatable GUI_NAME = keyed("gui-name");

    /** New Claim */
    public static final Translatable GUI_MAIN_NEW = keyed("gui-main-new");

    /** Edit Trusted Players */
    public static final Translatable GUI_MAIN_EDIT_TRUST = keyed("gui-main-edit-trust");

    /** Edit Claim Chunks */
    public static final Translatable GUI_MAIN_EDIT_CHUNK = keyed("gui-main-edit-chunk");

    /** Rename Claim */
    public static final Translatable GUI_MAIN_RENAME_CHUNK = keyed("gui-main-rename-chunk");

    /** Edit Claim Permissions */
    public static final Translatable GUI_MAIN_EDIT_PERM = keyed("gui-main-edit-perm");

    /** Transfer Owner */
    public static final Translatable GUI_MAIN_TRANSFER_OWNER = keyed("gui-main-transfer-owner");

    /** Clear All Claims */
    public static final Translatable GUI_MAIN_CLEAR_ALL = keyed("gui-main-clear-all");

    /** Delete Claim */
    public static final Translatable GUI_MAIN_DELETE = keyed("gui-main-delete");

    /** Version Info */
    public static final Translatable GUI_MAIN_VERSION = keyed("gui-main-version");

    /** Exit */
    public static final Translatable GUI_MAIN_EXIT = keyed("gui-main-exit");

    /** Version */
    public static final Translatable GUI_VINF_VERSION = keyed("gui-vinf-version");

    /** Made for MC Version $1 */
    public static final Translatable GUI_VINF_MC_VERSION = keyed("gui-vinf-mc-version");

    /** Unspecified */
    public static final Translatable GUI_VINF_MC_VERSION_UNSPECIFIED = keyed("gui-vinf-mc-version-unspecified");

    /** Author */
    public static final Translatable GUI_VINF_AUTHOR = keyed("gui-vinf-author");

    /** Back */
    public static final Translatable GUI_VINF_BACK = keyed("gui-vinf-back");

    /** Yes */
    public static final Translatable GUI_TX_YES = keyed("gui-tx-yes");

    /** I am sure that I want */
    public static final Translatable GUI_TX_YES_LINE1 = keyed("gui-tx-yes-line1");

    /** to transfer ownership to */
    public static final Translatable GUI_TX_YES_LINE2 = keyed("gui-tx-yes-line2");

    /** this user */
    public static final Translatable GUI_TX_YES_LINE3 = keyed("gui-tx-yes-line3");

    /** No */
    public static final Translatable GUI_TX_NO = keyed("gui-tx-no");

    /** Take me back to */
    public static final Translatable GUI_TX_NO_LINE1 = keyed("gui-tx-no-line1");

    /** safety! */
    public static final Translatable GUI_TX_NO_LINE2 = keyed("gui-tx-no-line2");

    /** Enter a player name in the chat to transfer the claim to. */
    public static final Translatable GUI_TX_PROMPT = keyed("gui-tx-prompt");

    /** * Cannot find that player! */
    public static final Translatable GUI_TX_PROMPT_FAIL = keyed("gui-tx-prompt-fail");

    /** * Ownership transferred! */
    public static final Translatable GUI_TX_SUCCESS = keyed("gui-tx-success");

    /** Previous Page */
    public static final Translatable GUI_COMB_PREVIOUS = keyed("gui-comb-previous");

    /** Next Page */
    public static final Translatable GUI_COMB_NEXT = keyed("gui-comb-next");

    /** Add Player */
    public static final Translatable GUI_COMB_ADD = keyed("gui-comb-add");

    /** Back */
    public static final Translatable GUI_COMB_BACK = keyed("gui-comb-back");

    /** Enter the player name in the chat to add. */
    public static final Translatable GUI_COMB_PROMPT = keyed("gui-comb-prompt");

    /** * Couldn't find a player with that name. */
    public static final Translatable GUI_COMB_PROMPT_FAIL = keyed("gui-comb-prompt-fail");

    /** Player successfully added. */
    public static final Translatable GUI_COMB_PROMPT_SUCCESS = keyed("gui-comb-prompt-success");

    /** General Permissions */
    public static final Translatable GUI_PERM_GENERAL = keyed("gui-perm-general");

    /** Manage permissions for */
    public static final Translatable GUI_PERM_GENERAL_LINE1 = keyed("gui-perm-general-line1");

    /** general groups (trusted */
    public static final Translatable GUI_PERM_GENERAL_LINE2 = keyed("gui-perm-general-line2");

    /** players, veterans, etc) */
    public static final Translatable GUI_PERM_GENERAL_LINE3 = keyed("gui-perm-general-line3");

    /** Player Permissions */
    public static final Translatable GUI_PERM_PLAYER = keyed("gui-perm-player");

    /** Manage per-player */
    public static final Translatable GUI_PERM_PLAYER_LINE1 = keyed("gui-perm-player-line1");

    /** permission exceptions */
    public static final Translatable GUI_PERM_PLAYER_LINE2 = keyed("gui-perm-player-line2");

    /** (for instance, stop */
    public static final Translatable GUI_PERM_PLAYER_LINE3 = keyed("gui-perm-player-line3");

    /** a specific player */
    public static final Translatable GUI_PERM_PLAYER_LINE4 = keyed("gui-perm-player-line4");

    /** from entering) */
    public static final Translatable GUI_PERM_PLAYER_LINE5 = keyed("gui-perm-player-line5");

    /** Back */
    public static final Translatable GUI_PERM_BACK = keyed("gui-perm-back");

    /** None */
    public static final Translatable GUI_PERM_TL_NONE = keyed("gui-perm-tl-none");

    /** Nobody except you */
    public static final Translatable GUI_PERM_TL_NONE_LINE1 = keyed("gui-perm-tl-none-line1");

    /** has this permission. */
    public static final Translatable GUI_PERM_TL_NONE_LINE2 = keyed("gui-perm-tl-none-line2");

    /** Trusted */
    public static final Translatable GUI_PERM_TL_TRUSTED = keyed("gui-perm-tl-trusted");

    /** Only players you have */
    public static final Translatable GUI_PERM_TL_TRUSTED_LINE1 = keyed("gui-perm-tl-trusted-line1");

    /** specifically trusted */
    public static final Translatable GUI_PERM_TL_TRUSTED_LINE2 = keyed("gui-perm-tl-trusted-line2");

    /** have this permission. */
    public static final Translatable GUI_PERM_TL_TRUSTED_LINE3 = keyed("gui-perm-tl-trusted-line3");

    /** Veterans */
    public static final Translatable GUI_PERM_TL_VETERANS = keyed("gui-perm-tl-veterans");

    /** Only players who have */
    public static final Translatable GUI_PERM_TL_VETERANS_LINE1 = keyed("gui-perm-tl-veterans-line1");

    /** played for some time */
    public static final Translatable GUI_PERM_TL_VETERANS_LINE2 = keyed("gui-perm-tl-veterans-line2");

    /** have this permission. */
    public static final Translatable GUI_PERM_TL_VETERANS_LINE3 = keyed("gui-perm-tl-veterans-line3");

    /** All */
    public static final Translatable GUI_PERM_TL_ALL = keyed("gui-perm-tl-all");

    /** All players can access */
    public static final Translatable GUI_PERM_TL_ALL_LINE1 = keyed("gui-perm-tl-all-line1");

    /** this permission. */
    public static final Translatable GUI_PERM_TL_ALL_LINE2 = keyed("gui-perm-tl-all-line2");

    /** Enabled */
    public static final Translatable GUI_PERM_ENABLED = keyed("gui-perm-enabled");

    /** Make this player have */
    public static final Translatable GUI_PERM_ENABLED_LINE1 = keyed("gui-perm-enabled-line1");

    /** this permission. */
    public static final Translatable GUI_PERM_ENABLED_LINE2 = keyed("gui-perm-enabled-line2");

    /** Disabled */
    public static final Translatable GUI_PERM_DISABLED = keyed("gui-perm-disabled");

    /** Unset this permission for */
    public static final Translatable GUI_PERM_DISABLED_LINE1 = keyed("gui-perm-disabled-line1");

    /** this player. Permission will */
    public static final Translatable GUI_PERM_DISABLED_LINE2 = keyed("gui-perm-disabled-line2");

    /** defer to general groups. */
    public static final Translatable GUI_PERM_DISABLED_LINE3 = keyed("gui-perm-disabled-line3");

    /** Confirm */
    public static final Translatable GUI_NEW_CONFIRM = keyed("gui-new-confirm");

    /** Create a new claim */
    public static final Translatable GUI_NEW_CONFIRM_LINE1 = keyed("gui-new-confirm-line1");

    /** starting in your current */
    public static final Translatable GUI_NEW_CONFIRM_LINE2 = keyed("gui-new-confirm-line2");

    /** chunk. */
    public static final Translatable GUI_NEW_CONFIRM_LINE3 = keyed("gui-new-confirm-line3");

    /** Cancel */
    public static final Translatable GUI_NEW_CANCEL = keyed("gui-new-cancel");

    /** Return to the */
    public static final Translatable GUI_NEW_CANCEL_LINE1 = keyed("gui-new-cancel-line1");

    /** main menu. */
    public static final Translatable GUI_NEW_CANCEL_LINE2 = keyed("gui-new-cancel-line2");

    /** * This chunk is already claimed! */
    public static final Translatable GUI_NEW_CLAIMED = keyed("gui-new-claimed");

    /** * You cannot make a new claim in a blacklisted world! */
    public static final Translatable GUI_NEW_DISALLOWED = keyed("gui-new-disallowed");

    /** * You've reached your maximum number of claims! Try deleting some. */
    public static final Translatable GUI_NEW_MAX_CLAIMS = keyed("gui-new-max-claims");

    /** * Can't create this claim, it will exceed your maximum number of chunks. */
    public static final Translatable GUI_NEW_MAX_CHUNKS = keyed("gui-new-max-chunks");

    /** * Created new claim $1 */
    public static final Translatable GUI_NEW_SUCCESS = keyed("gui-new-success");

    /** * You can't have claims across worlds! */
    public static final Translatable GUI_EDIT_CHUNK_FAIL = keyed("gui-edit-chunk-fail");

    /** * You are in a blacklisted world */
    public static final Translatable GUI_EDIT_CHUNK_DISALLOWED = keyed("gui-edit-chunk-disallowed");

    /** Enter a new name for the claim in the Chat. */
    public static final Translatable GUI_RENAME_CHUNK_PROMPT = keyed("gui-rename-chunk-prompt");

    /** * Name too long! Has to be less than 50 characters */
    public static final Translatable GUI_RENAME_CHUNK_FAIL = keyed("gui-rename-chunk-fail");

    /** Claim renamed successfully. */
    public static final Translatable GUI_RENAME_CHUNK_SUCCESS = keyed("gui-rename-chunk-success");

    /** Yes, I am sure */
    public static final Translatable GUI_CLEAR_YES = keyed("gui-clear-yes");

    /** I'm really sure! */
    public static final Translatable GUI_CLEAR_YES2 = keyed("gui-clear-yes2");

    /** This action cannot */
    public static final Translatable GUI_CLEAR_YES_LINE1 = keyed("gui-clear-yes-line1");

    /** be undone! */
    public static final Translatable GUI_CLEAR_YES_LINE2 = keyed("gui-clear-yes-line2");

    /** No, take me back */
    public static final Translatable GUI_CLEAR_NO = keyed("gui-clear-no");

    /** Keep your claims the */
    public static final Translatable GUI_CLEAR_NO_LINE1 = keyed("gui-clear-no-line1");

    /** way they are */
    public static final Translatable GUI_CLEAR_NO_LINE2 = keyed("gui-clear-no-line2");

    /** Owned by $1 */
    public static final Translatable GUI_SEL_OWNED = keyed("gui-sel-owned");

    /** $1 chunk */
    public static final Translatable GUI_SEL_CHUNK_COUNT = keyed("gui-sel-chunk-count");

    /** $1 chunks */
    public static final Translatable GUI_SEL_CHUNK_COUNT_PLURAL = keyed("gui-sel-chunk-count-plural");

    /** Chunk #1 at X=$1, Z=$2 */
    public static final Translatable GUI_SEL_FIRST_CHUNK = keyed("gui-sel-first-chunk");

    /** Currently within */
    public static final Translatable GUI_SEL_WITHIN = keyed("gui-sel-within");

    /** Previous */
    public static final Translatable GUI_SEL_PREVIOUS = keyed("gui-sel-previous");

    /** Search */
    public static final Translatable GUI_SEL_SEARCH = keyed("gui-sel-search");

    /** Cancel */
    public static final Translatable GUI_SEL_CANCEL = keyed("gui-sel-cancel");

    /** Next */
    public static final Translatable GUI_SEL_NEXT = keyed("gui-sel-next");

    /** Enter search term in the chat to search. */
    public static final Translatable GUI_SEL_PROMPT = keyed("gui-sel-prompt");

    /** * You must be a player to run this command! */
    public static final Translatable CMDMGR_ERR_PLAYER = keyed("cmdmgr-err-player");

    /** * Not enough arguments! This command requires at least $1 */
    public static final Translatable CMDMGR_ERR_MIN_ARGS = keyed("cmdmgr-err-min-args");

    /** * Too many arguments! This command takes at most $1 */
    public static final Translatable CMDMGR_ERR_MAX_ARGS = keyed("cmdmgr-err-max-args");

    /** * Bad arguments! See help page for more info */
    public static final Translatable CMDMGR_ERR_MALFORMED = keyed("cmdmgr-err-malformed");

    /** * An unexpected exception ($1) occurred while executing this command. */
    public static final Translatable CMDMGR_ERR_UNEXPECTED = keyed("cmdmgr-err-unexpected");

    /** Could not register command "$1", does not exist in plugin.yml */
    public static final Translatable CMDMGR_ERR_UNDEFINED = keyed("cmdmgr-err-undefined");

    /** Could not access constructor for class $1, see details below */
    public static final Translatable CMDMGR_ERR_REFLECT = keyed("cmdmgr-err-reflect");

    /** XClaim main command */
    public static final Translatable CMD_XC_DESCRIPTION = keyed("cmd-xc-description");

    /** importclaims */
    public static final Translatable CMD_IMPORT_NAME = keyed("cmd-import-name");

    /** Imports claims from the ClaimChunk plugin */
    public static final Translatable CMD_IMPORT_DESCRIPTION = keyed("cmd-import-description");

    /** The ClaimChunk plugin does not seem to be enabled. */
    public static final Translatable CMD_IMPORT_ERR_DISABLED = keyed("cmd-import-err-disabled");

    /** * ClaimChunk does not appear to be installed and enabled */
    public static final Translatable CMD_IMPORT_ERR_INSTALLED = keyed("cmd-import-err-installed");

    /** Getting data handler... */
    public static final Translatable CMD_IMPORT_STATUS_HANDLER = keyed("cmd-import-status-handler");

    /** Failed. See details in console. */
    public static final Translatable CMD_IMPORT_ERR_REFLECT = keyed("cmd-import-err-reflect");

    /** Processing world $1 */
    public static final Translatable CMD_IMPORT_STATUS_WORLD = keyed("cmd-import-status-world");

    /** Found claimed chunk at $1, $2 */
    public static final Translatable CMD_IMPORT_STATUS_CHUNK = keyed("cmd-import-status-chunk");

    /** Performing flood fill algorithm */
    public static final Translatable CMD_IMPORT_STATUS_FILL = keyed("cmd-import-status-fill");

    /** Creating claims for player $1 */
    public static final Translatable CMD_IMPORT_STATUS_PLAYER = keyed("cmd-import-status-player");

    /** Success */
    public static final Translatable CMD_IMPORT_STATUS_SUCCESS = keyed("cmd-import-status-success");

    /** Processed all worlds successfully. Disabling ClaimChunk plugin... */
    public static final Translatable CMD_IMPORT_STATUS_DISABLING = keyed("cmd-import-status-disabling");

    /** Done */
    public static final Translatable CMD_IMPORT_STATUS_DONE = keyed("cmd-import-status-done");

    /** update */
    public static final Translatable CMD_UPDATE_NAME = keyed("cmd-update-name");

    /** Searches for updates for XClaim online */
    public static final Translatable CMD_UPDATE_DESCRIPTION = keyed("cmd-update-description");

    /** Whether or not to proceed with the update once found */
    public static final Translatable CMD_UPDATE_ARG_PROCEED_DESCRIPTION = keyed("cmd-update-arg-proceed-description");

    /** yes */
    public static final Translatable CMD_UPDATE_ARG_PROCEED_YES = keyed("cmd-update-arg-proceed-yes");

    /** no */
    public static final Translatable CMD_UPDATE_ARG_PROCEED_NO = keyed("cmd-update-arg-proceed-no");

    /** * You don't have permission to run this command! */
    public static final Translatable CMD_UPDATE_ERR_PERMS = keyed("cmd-update-err-perms");

    /** * You do not have permission to update XClaim! */
    public static final Translatable CMD_UPDATE_ERR_PERMS2 = keyed("cmd-update-err-perms2");

    /** * Declined update. */
    public static final Translatable CMD_UPDATE_DECLINED = keyed("cmd-update-declined");

    /** * Looking for updates... */
    public static final Translatable CMD_UPDATE_SEARCHING = keyed("cmd-update-searching");

    /** * Failed to find any version to update to. See console for more details. */
    public static final Translatable CMD_UPDATE_ERR_CHECK = keyed("cmd-update-err-check");

    /** * No valid versions to update to found. */
    public static final Translatable CMD_UPDATE_NONE = keyed("cmd-update-none");

    /** * Installing update... */
    public static final Translatable CMD_UPDATE_START = keyed("cmd-update-start");

    /** * Failed to update. See console for more details. */
    public static final Translatable CMD_UPDATE_ERR_UNEXPECTED = keyed("cmd-update-err-unexpected");

    /** * Updated successfully! Changes will reflect on next restart. Restarting soon is recommended to avoid any unpredictable bugs. */
    public static final Translatable CMD_UPDATE_SUCCESS = keyed("cmd-update-success");

    /** NEW: Click  */
    public static final Translatable CMD_UPDATE_PROMOTE_RESTART_PLAYER_PRE = keyed("cmd-update-promote-restart-player-pre");

    /** here */
    public static final Translatable CMD_UPDATE_PROMOTE_RESTART_PLAYER_CLICK = keyed("cmd-update-promote-restart-player-click");

    /**  to restart XClaim without restarting the server (EXPERIMENTAL) */
    public static final Translatable CMD_UPDATE_PROMOTE_RESTART_PLAYER_POST = keyed("cmd-update-promote-restart-player-post");

    /** NEW: Run /xclaim restart yes to restart XClaim without restarting the server (EXPERIMENTAL) */
    public static final Translatable CMD_UPDATE_PROMOTE_RESTART_CONSOLE = keyed("cmd-update-promote-restart-console");

    /** * You are already using the latest compatible version of XClaim! */
    public static final Translatable CMD_UPDATE_REDUNDANT = keyed("cmd-update-redundant");

    /** * Found version $1 */
    public static final Translatable CMD_UPDATE_FOUND = keyed("cmd-update-found");

    /** * Use /xclaim update yes to install this version. */
    public static final Translatable CMD_UPDATE_CONFIRM_CONSOLE = keyed("cmd-update-confirm-console");

    /** Install this version? */
    public static final Translatable CMD_UPDATE_CONFIRM_PLAYER_PROMPT = keyed("cmd-update-confirm-player-prompt");

    /** Yes */
    public static final Translatable CMD_UPDATE_CONFIRM_PLAYER_YES = keyed("cmd-update-confirm-player-yes");

    /** No */
    public static final Translatable CMD_UPDATE_CONFIRM_PLAYER_NO = keyed("cmd-update-confirm-player-no");

    /** restart */
    public static final Translatable CMD_RESTART_NAME = keyed("cmd-restart-name");

    /** Restarts XClaim without restarting the server */
    public static final Translatable CMD_RESTART_DESCRIPTION = keyed("cmd-restart-description");

    /** If yes, restarts without confirming */
    public static final Translatable CMD_RESTART_ARG_CONFIRM_DESCRIPTION = keyed("cmd-restart-arg-confirm-description");

    /** yes */
    public static final Translatable CMD_RESTART_ARG_CONFIRM_YES = keyed("cmd-restart-arg-confirm-yes");

    /** no */
    public static final Translatable CMD_RESTART_ARG_CONFIRM_NO = keyed("cmd-restart-arg-confirm-no");

    /** * You don't have permission to run this command! */
    public static final Translatable CMD_RESTART_ERR_PERM = keyed("cmd-restart-err-perm");

    /** Disabling XClaim... */
    public static final Translatable CMD_RESTART_STATUS_DISABLING = keyed("cmd-restart-status-disabling");

    /** Failed to remove XClaim from plugin manager. Continuing... */
    public static final Translatable CMD_RESTART_WARN_PM = keyed("cmd-restart-warn-pm");

    /** Enabling XClaim... */
    public static final Translatable CMD_RESTART_STATUS_ENABLING = keyed("cmd-restart-status-enabling");

    /** Failed to load XClaim */
    public static final Translatable CMD_RESTART_ERR_LOAD = keyed("cmd-restart-err-load");

    /** Failed to enable XClaim */
    public static final Translatable CMD_RESTART_ERR_ENABLE = keyed("cmd-restart-err-enable");

    /** Enabled XClaim version $1 */
    public static final Translatable CMD_RESTART_STATUS_SUCCESS = keyed("cmd-restart-status-success");

    /** WARNING! */
    public static final Translatable CMD_RESTART_WARN_HEADER = keyed("cmd-restart-warn-header");

    /** This feature is experimental. */
    public static final Translatable CMD_RESTART_WARN_BODY = keyed("cmd-restart-warn-body");

    /** Click  */
    public static final Translatable CMD_RESTART_CONFIRM_PLAYER_PRE = keyed("cmd-restart-confirm-player-pre");

    /** here */
    public static final Translatable CMD_RESTART_CONFIRM_PLAYER_CLICK = keyed("cmd-restart-confirm-player-click");

    /**  to continue anyway. */
    public static final Translatable CMD_RESTART_CONFIRM_PLAYER_POST = keyed("cmd-restart-confirm-player-post");

    /** Run /xclaim restart yes to continue anyway. */
    public static final Translatable CMD_RESTART_CONFIRM_CONSOLE = keyed("cmd-restart-confirm-console");

    /** list */
    public static final Translatable CMD_LIST_NAME = keyed("cmd-list-name");

    /** Lists the claims of the specified player */
    public static final Translatable CMD_LIST_DESCRIPTION = keyed("cmd-list-description");

    /** The player to list the claims of, or yourself if not specified and you are a player */
    public static final Translatable CMD_LIST_ARG_PLAYER_DESCRIPTION = keyed("cmd-list-arg-player-description");

    /** The maximum chunks to show from each claim, defaults to 3 */
    public static final Translatable CMD_LIST_ARG_CHUNKS_DESCRIPTION = keyed("cmd-list-arg-chunks-description");

    /** * You need to specify a player (you are not a player)! */
    public static final Translatable CMD_LIST_ERR_PLAYER = keyed("cmd-list-err-player");

    /** $1 has no claims */
    public static final Translatable CMD_LIST_NONE = keyed("cmd-list-none");

    /** Claim #$1: $2 */
    public static final Translatable CMD_LIST_CLAIM_HEADER = keyed("cmd-list-claim-header");

    /** Chunk at X=$1, Z=$2 */
    public static final Translatable CMD_LIST_CLAIM_CHUNK = keyed("cmd-list-claim-chunk");

    /** and $1 more... */
    public static final Translatable CMD_LIST_CLAIM_MORE = keyed("cmd-list-claim-more");

    /** info */
    public static final Translatable CMD_INFO_NAME = keyed("cmd-info-name");

    /** Prints out basic info about XClaim */
    public static final Translatable CMD_INFO_DESCRIPTION = keyed("cmd-info-description");

    /** Made by $1 */
    public static final Translatable CMD_INFO_AUTHOR = keyed("cmd-info-author");

    /** Version $1 */
    public static final Translatable CMD_INFO_VERSION = keyed("cmd-info-version");

    /** API Version $1 */
    public static final Translatable CMD_INFO_API_VERSION = keyed("cmd-info-apiVersion");

    /** 1 total claim covering 1 chunk */
    public static final Translatable CMD_INFO_CLAIMS_NOT_PLURAL = keyed("cmd-info-claims-not-plural");

    /** 1 total claim covering $2 chunks */
    public static final Translatable CMD_INFO_CLAIMS_CHUNK_PLURAL = keyed("cmd-info-claims-chunk-plural");

    /** $1 total claims covering $2 chunks */
    public static final Translatable CMD_INFO_CLAIMS_BOTH_PLURAL = keyed("cmd-info-claims-both-plural");

    /** help */
    public static final Translatable CMD_HELP_NAME = keyed("cmd-help-name");

    /** Provides a list of possible commands or detailed info for a specific command */
    public static final Translatable CMD_HELP_DESCRIPTION = keyed("cmd-help-description");

    /** Page number or command name */
    public static final Translatable CMD_HELP_ARG_NAME = keyed("cmd-help-arg-name");

    /** The page of help to view, or the name of the command to view detailed information about */
    public static final Translatable CMD_HELP_ARG_DESCRIPTION = keyed("cmd-help-arg-description");

    /** * Can't find that command */
    public static final Translatable CMD_HELP_ERR_404 = keyed("cmd-help-err-404");

    /** No arguments */
    public static final Translatable CMD_HELP_NO_ARGS = keyed("cmd-help-no-args");

    /** Page $1 */
    public static final Translatable CMD_HELP_PAGE = keyed("cmd-help-page");

    /** gui */
    public static final Translatable CMD_GUI_NAME = keyed("cmd-gui-name");

    /** An accessible gui for all XClaim functions */
    public static final Translatable CMD_GUI_DESCRIPTION = keyed("cmd-gui-description");

    /** * You must exit the chunk editor before using the GUI. */
    public static final Translatable CMD_GUI_ERR_RESTRICTED = keyed("cmd-gui-err-restricted");

    /** current */
    public static final Translatable CMD_CURRENT_NAME = keyed("cmd-current-name");

    /** Gets info about the current claim you are in */
    public static final Translatable CMD_CURRENT_DESCRIPTION = keyed("cmd-current-description");

    /** * You are not in a claim! */
    public static final Translatable CMD_CURRENT_ERR_404 = keyed("cmd-current-err-404");

    /** Unset */
    public static final Translatable CMD_CURRENT_WORLD_UNSET = keyed("cmd-current-world-unset");

    /** = $1 = */
    public static final Translatable CMD_CURRENT_OUTPUT_LINE1 = keyed("cmd-current-output-line1");

    /** Owned by $1 */
    public static final Translatable CMD_CURRENT_OUTPUT_LINE2 = keyed("cmd-current-output-line2");

    /** In world $1 */
    public static final Translatable CMD_CURRENT_OUTPUT_LINE3 = keyed("cmd-current-output-line3");

    /** With $1 chunks */
    public static final Translatable CMD_CURRENT_OUTPUT_LINE4_PLURAL = keyed("cmd-current-output-line4-plural");

    /** With $1 chunk */
    public static final Translatable CMD_CURRENT_OUTPUT_LINE4_SINGULAR = keyed("cmd-current-output-line4-singular");

    /** clear */
    public static final Translatable CMD_CLEAR_NAME = keyed("cmd-clear-name");

    /** Removes all existing claims for a player */
    public static final Translatable CMD_CLEAR_DESCRIPTION = keyed("cmd-clear-description");

    /** The player to clear the claims of, or yourself if not specified and you are a player */
    public static final Translatable CMD_CLEAR_ARG_PLAYER_DESCRIPTION = keyed("cmd-clear-arg-player-description");

    /** If yes, then this command will execute without confirmation */
    public static final Translatable CMD_CLEAR_ARG_CONFIRM_DESCRIPTION = keyed("cmd-clear-arg-confirm-description");

    /** yes */
    public static final Translatable CMD_CLEAR_ARG_CONFIRM_YES = keyed("cmd-clear-arg-confirm-yes");

    /** no */
    public static final Translatable CMD_CLEAR_ARG_CONFIRM_NO = keyed("cmd-clear-arg-confirm-no");

    /** * You need to specify a player (you are not a player)! */
    public static final Translatable CMD_CLEAR_ERR_MISSING = keyed("cmd-clear-err-missing");

    /** * You don't have permission to clear other players' commands! */
    public static final Translatable CMD_CLEAR_ERR_PERM = keyed("cmd-clear-err-perm");

    /** Unknown */
    public static final Translatable CMD_CLEAR_PLAYER_UNKNOWN = keyed("cmd-clear-player-unknown");

    /** CONSOLE */
    public static final Translatable CMD_CLEAR_PLAYER_CONSOLE = keyed("cmd-clear-player-console");

    /** * Cleared all of $1's claims */
    public static final Translatable CMD_CLEAR_SUCCESS = keyed("cmd-clear-success");

    /** * Your claims were cleared by $1 */
    public static final Translatable CMD_CLEAR_NOTIFY = keyed("cmd-clear-notify");

    /** Are you sure you want to clear all of $1's claims? */
    public static final Translatable CMD_CLEAR_PROMPT = keyed("cmd-clear-prompt");

    /** Click  */
    public static final Translatable CMD_CLEAR_PROMPT_PLAYER_PRE = keyed("cmd-clear-prompt-player-pre");

    /** here */
    public static final Translatable CMD_CLEAR_PROMPT_PLAYER_CLICK = keyed("cmd-clear-prompt-player-click");

    /**  to confirm */
    public static final Translatable CMD_CLEAR_PROMPT_PLAYER_POST = keyed("cmd-clear-prompt-player-post");

    /** Run $1 to confirm */
    public static final Translatable CMD_CLEAR_PROMPT_CONSOLE = keyed("cmd-clear-prompt-console");

    /** chunks */
    public static final Translatable CMD_CHUNKS_NAME = keyed("cmd-chunks-name");

    /** Opens the claim chunk editor. If a claim name is specified, then it will edit that claim. Otherwise, it uses the current residing claim. */
    public static final Translatable CMD_CHUNKS_DESCRIPTION = keyed("cmd-chunks-description");

    /** Claim name */
    public static final Translatable CMD_CHUNKS_ARG_NAME = keyed("cmd-chunks-arg-name");

    /** Name of the claim to edit. If absent, the current residing claim is assumed. */
    public static final Translatable CMD_CHUNKS_ARG_DESCRIPTION = keyed("cmd-chunks-arg-description");

    /** * You are already in the chunk editor! Exit it first! */
    public static final Translatable CMD_CHUNKS_ERR_STATE = keyed("cmd-chunks-err-state");

    /** * You aren't currently in a claim! */
    public static final Translatable CMD_CHUNKS_ERR_404 = keyed("cmd-chunks-err-404");

    /** * You do not have permission to manage this claim! */
    public static final Translatable CMD_CHUNKS_ERR_PERM = keyed("cmd-chunks-err-perm");

    /** * You cannot edit claims in a blacklisted world */
    public static final Translatable CMD_CHUNKS_ERR_DISALLOWED = keyed("cmd-chunks-err-disallowed");

    /** Editing $1 */
    public static final Translatable CMD_CHUNKS_SUCCESS = keyed("cmd-chunks-success");

    /** Text */
    public static final Translatable ARG_STRING_NAME = keyed("arg-string-name");

    /** text */
    public static final Translatable ARG_STRING_SAMPLE = keyed("arg-string-sample");

    /** Integer between $1 and $2 */
    public static final Translatable ARG_RANGE_NAME = keyed("arg-range-name");

    /** Online Player */
    public static final Translatable ARG_PLAYER_NAME = keyed("arg-player-name");

    /** Player */
    public static final Translatable ARG_OFFLINE_PLAYER_NAME = keyed("arg-offlinePlayer-name");

    /** Material */
    public static final Translatable ARG_MATERIAL_NAME = keyed("arg-material-name");

    /** Item */
    public static final Translatable ARG_ITEM_MATERIAL_NAME = keyed("arg-itemMaterial-name");

    /** Block */
    public static final Translatable ARG_BLOCK_MATERIAL_NAME = keyed("arg-blockMaterial-name");

    /** Integer */
    public static final Translatable ARG_INT_NAME = keyed("arg-int-name");

    /** Decimal */
    public static final Translatable ARG_FLOAT_NAME = keyed("arg-float-name");

    /** Many */
    public static final Translatable ARG_COMBO_MANY = keyed("arg-combo-many");

    /** ,  */
    public static final Translatable ARG_COMBO_SEPARATOR = keyed("arg-combo-separator");

    /**  or  */
    public static final Translatable ARG_COMBO_OR = keyed("arg-combo-or");

    /** Choice of  */
    public static final Translatable ARG_CHOICE_ROOT = keyed("arg-choice-root");

    /** Nothing */
    public static final Translatable ARG_CHOICE_NOTHING = keyed("arg-choice-nothing");

    /** Many */
    public static final Translatable ARG_CHOICE_MANY = keyed("arg-choice-many");

    /** ,  */
    public static final Translatable ARG_CHOICE_SEPARATOR = keyed("arg-choice-separator");

    /**  or  */
    public static final Translatable ARG_CHOICE_OR = keyed("arg-choice-or");

    /** Entering $1's $2 */
    public static final Translatable MOVE_ENTER = keyed("move-enter");

    /** Leaving $1 */
    public static final Translatable MOVE_EXIT = keyed("move-exit");

    /** WARNING: You have $1 claims in blacklisted worlds that will be removed soon */
    public static final Translatable GRACE_ALERT = keyed("grace-alert");

    /** Your claim named $1 was removed due to being in a blacklisted world */
    public static final Translatable GRACE_REMOVE = keyed("grace-remove");

    /** * You cannot create a claim at this time. */
    public static final Translatable EVENT_FAIL_CREATE_CLAIM = keyed("event-fail-create-claim");

    /** * Cannot create this claim ($1) */
    public static final Translatable EVENT_FAIL_CREATE_CLAIM_SINGLE = keyed("event-fail-create-claim-single");

    /** * You cannot delete a claim at this time. */
    public static final Translatable EVENT_FAIL_DELETE_CLAIM = keyed("event-fail-delete-claim");

    /** * Cannot delete this claim ($1) */
    public static final Translatable EVENT_FAIL_DELETE_CLAIM_SINGLE = keyed("event-fail-delete-claim-single");

    /** * You cannot add chunks to a claim at this time. */
    public static final Translatable EVENT_FAIL_ADD_CHUNKS = keyed("event-fail-add-chunks");

    /** * Cannot add chunks to this claim ($1) */
    public static final Translatable EVENT_FAIL_ADD_CHUNKS_SINGLE = keyed("event-fail-add-chunks-single");

    /** * You cannot add a chunk to a claim at this time. */
    public static final Translatable EVENT_FAIL_ADD_CHUNK = keyed("event-fail-add-chunk");

    /** * Cannot add a chunk to this claim ($1) */
    public static final Translatable EVENT_FAIL_ADD_CHUNK_SINGLE = keyed("event-fail-add-chunk-single");

    /** * You cannot remove chunks from a claim at this time. */
    public static final Translatable EVENT_FAIL_REMOVE_CHUNKS = keyed("event-fail-remove-chunks");

    /** * Cannot remove chunks from this claim ($1) */
    public static final Translatable EVENT_FAIL_REMOVE_CHUNKS_SINGLE = keyed("event-fail-remove-chunks-single");

    /** * You cannot remove a chunk from a claim at this time. */
    public static final Translatable EVENT_FAIL_REMOVE_CHUNK = keyed("event-fail-remove-chunk");

    /** * Cannot remove a chunk from this claim ($1) */
    public static final Translatable EVENT_FAIL_REMOVE_CHUNK_SINGLE = keyed("event-fail-remove-chunk-single");

    /** * You cannot transfer the claim owner at this time. */
    public static final Translatable EVENT_FAIL_TRANSFER_OWNER = keyed("event-fail-transfer-owner");

    /** * Cannot transfer this claim's owner ($1) */
    public static final Translatable EVENT_FAIL_TRANSFER_OWNER_SINGLE = keyed("event-fail-transfer-owner-single");

    /** * You cannot set this permission on the claim at this time. */
    public static final Translatable EVENT_FAIL_SET_PERMISSION = keyed("event-fail-set-permission");

    /** * Cannot set permission on this claim ($1) */
    public static final Translatable EVENT_FAIL_SET_PERMISSION_SINGLE = keyed("event-fail-set-permission-single");

    /** * You cannot grant a user permission at this time. */
    public static final Translatable EVENT_FAIL_GRANT_USER_PERMISSION = keyed("event-fail-grant-user-permission");

    /** * Cannot grant user permission on this claim ($1) */
    public static final Translatable EVENT_FAIL_GRANT_USER_PERMISSION_SINGLE = keyed("event-fail-grant-user-permission-single");

    /** * You cannot revoke a user permission at this time. */
    public static final Translatable EVENT_FAIL_REVOKE_USER_PERMISSION = keyed("event-fail-revoke-user-permission");

    /** * Cannot revoke user permission on this claim ($1) */
    public static final Translatable EVENT_FAIL_REVOKE_USER_PERMISSION_SINGLE = keyed("event-fail-revoke-user-permission-single");

    /** Unknown */
    public static final Translatable UNKNOWN = keyed("unknown");

}
