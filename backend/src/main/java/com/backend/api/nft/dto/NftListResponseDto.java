package com.backend.api.nft.dto;

import com.backend.domain.nft.entity.Nft;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NftListResponseDto {

    private final String owner;
    private final Long nftId;
    private final String imageUrl;
    private final String title;
    private final String content;
    private final String tokenId;

    public NftListResponseDto(Nft nft) {
        this.owner = nft.getMember().getNickname();
        this.title = nft.getTitle();
        this.nftId = nft.getNftId();
        this.imageUrl = nft.getNftImageUrl();
        this.content = nft.getContent();
        this.tokenId = nft.getTokenId();
    }

    @JsonCreator
    public NftListResponseDto(@JsonProperty("owner") String owner, @JsonProperty("title") String title, @JsonProperty("nftId") Long nftId,
                                @JsonProperty("imageUrl") String imageUrl, @JsonProperty("content") String content, @JsonProperty("tokenId") String tokenId) {
        this.owner = owner;
        this.title = title;
        this.nftId = nftId;
        this.imageUrl = imageUrl;
        this.content = content;
        this.tokenId = tokenId;
    }
}
