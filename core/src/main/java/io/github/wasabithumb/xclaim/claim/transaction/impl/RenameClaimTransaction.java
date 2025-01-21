package io.github.wasabithumb.xclaim.claim.transaction.impl;

import io.github.wasabithumb.xclaim.claim.ClaimMutationContext;
import io.github.wasabithumb.xclaim.claim.transaction.ClaimTransaction;
import io.github.wasabithumb.xclaim.i18n.I18N;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class RenameClaimTransaction extends ClaimTransaction {

    private String newName = null;
    public RenameClaimTransaction(@NotNull ClaimMutationContext context) {
        super(context);
    }

    @Override
    protected void onCommit() {
        if (this.newName == null) return;
        if (this.newName.length() > 50 || this.manager.getByName(this.newName) != null) {
            this.langMessage(I18N.GUI_RENAME_CHUNK_FAIL);
            this.valid = false;
            return;
        }
        this.data.setName(this.newName);
    }

    @Contract("_ -> this")
    public @NotNull RenameClaimTransaction setNewName(@NotNull String newName) {
        if (newName.equals(this.data.getName())) return this;
        if (this.manageCheck()) return this;
        this.newName = newName;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull RenameClaimTransaction silent(boolean silent) {
        this.silent = silent;
        return this;
    }

}
