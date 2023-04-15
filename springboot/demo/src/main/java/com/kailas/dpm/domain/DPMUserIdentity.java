package com.kailas.dpm.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialEntity;
import lombok.Generated;
import lombok.NonNull;

import java.io.Serializable;

public class DPMUserIdentity implements PublicKeyCredentialEntity , Serializable {
    private final @NonNull String name;
    private final @NonNull String email;
    private final @NonNull ByteArray id;


    @JsonCreator
    private DPMUserIdentity(@JsonProperty("name") @NonNull String name, @JsonProperty("email") @NonNull String email, @JsonProperty("id") @NonNull ByteArray id) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else if (email == null) {
            throw new NullPointerException("email is marked non-null but is null");
        } else if (id == null) {
            throw new NullPointerException("id is marked non-null but is null");
        } else {
            this.name = name;
            this.email = email;
            this.id = id;
        }
    }

    public static DPMUserIdentity.DPMUserIdentityBuilder.MandatoryStages builder() {
        return new DPMUserIdentity.DPMUserIdentityBuilder.MandatoryStages();
    }

    @Generated
    public DPMUserIdentity.DPMUserIdentityBuilder toBuilder() {
        return (new DPMUserIdentity.DPMUserIdentityBuilder()).name(this.name).email(this.email).id(this.id);
    }

    @Generated
    public @NonNull String getName() {
        return this.name;
    }

    @Generated
    public @NonNull String getEmail() {
        return this.email;
    }

    @Generated
    public @NonNull ByteArray getId() {
        return this.id;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof DPMUserIdentity)) {
            return false;
        } else {
            DPMUserIdentity other;
            label44: {
                other = (DPMUserIdentity)o;
                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name == null) {
                        break label44;
                    }
                } else if (this$name.equals(other$name)) {
                    break label44;
                }

                return false;
            }

            Object this$email = this.getEmail();
            Object other$email = other.getEmail();
            if (this$email == null) {
                if (other$email != null) {
                    return false;
                }
            } else if (!this$email.equals(other$email)) {
                return false;
            }

            Object this$id = this.getId();
            Object other$id = other.getId();
            if (this$id == null) {
                if (other$id != null) {
                    return false;
                }
            } else if (!this$id.equals(other$id)) {
                return false;
            }

            return true;
        }
    }

    @Generated
    public int hashCode() {
        int result = 1;
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $email= this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "DPMUserIdentity(name=" + this.getName() + ", email=" + this.getEmail() + ", id=" + this.getId() + ")";
    }



    public static class DPMUserIdentityBuilder {
        @Generated
        private String name;
        @Generated
        private String email;
        @Generated
        private ByteArray id;

        @Generated
        DPMUserIdentityBuilder() {
        }

        @Generated
        public DPMUserIdentity.DPMUserIdentityBuilder name(@NonNull String name) {
            if (name == null) {
                throw new NullPointerException("name is marked non-null but is null");
            } else {
                this.name = name;
                return this;
            }
        }

        @Generated
        public DPMUserIdentity.DPMUserIdentityBuilder email(@NonNull String email) {
            if (email == null) {
                throw new NullPointerException("email is marked non-null but is null");
            } else {
                this.email = email;
                return this;
            }
        }

        @Generated
        public DPMUserIdentity.DPMUserIdentityBuilder id(@NonNull ByteArray id) {
            if (id == null) {
                throw new NullPointerException("id is marked non-null but is null");
            } else {
                this.id = id;
                return this;
            }
        }

        @Generated
        public DPMUserIdentity build() {
            return new DPMUserIdentity(this.name, this.email, this.id);
        }

        @Generated
        public String toString() {
            return "DPMUserIdentity.DPMUserIdentityBuilder(name=" + this.name + ", email=" + this.email + ", id=" + this.id + ")";
        }

        public static class MandatoryStages {
            private final DPMUserIdentity.DPMUserIdentityBuilder builder = new DPMUserIdentity.DPMUserIdentityBuilder();

            public MandatoryStages() {
            }

            public DPMUserIdentity.DPMUserIdentityBuilder.MandatoryStages.Step2 name(String name) {
                DPMUserIdentity.DPMUserIdentityBuilder.MandatoryStages.this.builder.name(name);
                return new DPMUserIdentity.DPMUserIdentityBuilder.MandatoryStages.Step2();
            }

            public class Step2 {
                public Step2() {
                }

                public DPMUserIdentity.DPMUserIdentityBuilder.MandatoryStages.Step3 email(String email) {
                    DPMUserIdentity.DPMUserIdentityBuilder.MandatoryStages.this.builder.email(email);
                    return DPMUserIdentity.DPMUserIdentityBuilder.MandatoryStages.this.new Step3();
                }
            }

            public class Step3 {
                public Step3() {
                }

                public DPMUserIdentity.DPMUserIdentityBuilder id(ByteArray id) {
                    return DPMUserIdentity.DPMUserIdentityBuilder.MandatoryStages.this.builder.id(id);
                }
            }
        }
    }
}
