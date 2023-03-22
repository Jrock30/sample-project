package com.sample.project.api.marketingMessage.dto;


import com.sample.project.api.marketingMessage.entity.AttachFileEntity;
import com.sample.project.common.jpa.intf.ChangeableToFromEntity;
import com.sample.project.common.jpa.model.AdminBaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author   	: user
 * @since    	: 2022/11/21
 * @desc     	: 첨부파일 dto
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AttachFileDto extends AdminBaseDto implements ChangeableToFromEntity<AttachFileEntity> {

    @Schema(description = "첨부파일 식별 번호")
    private Long attachFileNo;

    @Schema(description = "첨부 파일 명")
    private String attachFileName;

    @Schema(description = "물리 파일 명")
    private String pysicsFileName;

    @Schema(description = "파일 경로")
    private String filePath;

    @Schema(description = "파일 URL")
    private String fileUrl;

    @Schema(description = "썸네일 URL")
    private String thumbnailUrl;

    @Schema(description = "파일사이즈")
    private Long fileSize;

    @Schema(description = "MIME TYPE")
    private String mimeType;

    public AttachFileDto(AttachFileEntity attachFileEntity){
        from(attachFileEntity);
    }

    @Override
    public AttachFileEntity to() {
        return AttachFileEntity.builder()
                .attachFileNo(attachFileNo)
                .attachFileName(attachFileName)
                .pysicsFileName(pysicsFileName)
                .filePath(filePath)
                .fileUrl(fileUrl)
                .thumbnailUrl(thumbnailUrl)
                .fileSize(fileSize)
                .mimeType(mimeType)
                .regId(super.getRegId())
                .regDate(super.getRegDate())
                .updId(super.getUpdId())
                .updDate(super.getUpdDate())
                .build();
    }

    @Override
    public void from(AttachFileEntity entity) {
        this.attachFileNo = entity.getAttachFileNo();
        this.attachFileName = entity.getAttachFileName();
        this.pysicsFileName = entity.getPysicsFileName();
        this.filePath = entity.getFilePath();
        this.fileUrl =entity.getFileUrl();
        this.thumbnailUrl = entity.getThumbnailUrl();
        this.fileSize = entity.getFileSize();
        this.mimeType = entity.getMimeType();
        super.setRegId(entity.getRegId());
        super.setRegDate(entity.getRegDate());
        super.setUpdId(entity.getUpdId());
        super.setUpdDate(entity.getUpdDate());
    }
}
